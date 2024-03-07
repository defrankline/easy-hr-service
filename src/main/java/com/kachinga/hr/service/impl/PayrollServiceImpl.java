package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.*;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.domain.dto.LoanRepaymentRequestDto;
import com.kachinga.hr.messaging.Producer;
import com.kachinga.hr.repository.*;
import com.kachinga.hr.service.MiscEarningService;
import com.kachinga.hr.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {
    private final PayrollRepository payrollRepository;
    private final MonthlyDeductionRepository monthlyDeductionRepository;
    private final PayrollDeductionRepository payrollDeductionRepository;
    private final DeductionRepository deductionRepository;
    private final PayrollItemRepository payrollItemRepository;
    private final StaffRepository staffRepository;
    private final DeductionAccountRepository deductionAccountRepository;
    private final MiscEarningService miscEarningService;
    private final Producer producer;

    @Override
    public Mono<Payroll> save(Payroll payroll) {
        return payrollRepository.save(payroll);
    }

    @Override
    public Mono<Payroll> getById(Long id) {
        return payrollRepository.findById(id);
    }

    @Override
    public Mono<Payroll> getByCode(String code) {
        return payrollRepository.findByCode(code);
    }

    public Mono<DataDto<Payroll>> findAll(Long companyId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Mono<List<Payroll>> payrollsMono = payrollRepository.findByCompanyId(companyId, pageRequest).collectList();
        Mono<Long> countMono = payrollRepository.count();
        return Mono.zip(payrollsMono, countMono, DataDto::new);
    }

    public BigDecimal payAsYouEarn(BigDecimal taxableAmount) {
        if (taxableAmount.compareTo(new BigDecimal(270000)) <= 0) {
            return BigDecimal.ZERO;
        } else if (taxableAmount.compareTo(new BigDecimal(270000)) > 0 && taxableAmount.compareTo(new BigDecimal(520000)) <= 0) {
            BigDecimal balance = taxableAmount.subtract(new BigDecimal(270000));
            return ((balance.multiply(new BigDecimal(9))).divide(new BigDecimal(100), 9, RoundingMode.CEILING));
        } else if (taxableAmount.compareTo(new BigDecimal(520000)) > 0 && taxableAmount.compareTo(new BigDecimal(760000)) <= 0) {
            BigDecimal balance = taxableAmount.subtract(new BigDecimal(520000));
            return ((balance.multiply(new BigDecimal(20))).divide(new BigDecimal(100), 9, RoundingMode.CEILING)).add(new BigDecimal(22500));
        } else if (taxableAmount.compareTo(new BigDecimal(760000)) > 0 && taxableAmount.compareTo(new BigDecimal(1000000)) <= 0) {
            BigDecimal balance = taxableAmount.subtract(new BigDecimal(760000));
            return ((balance.multiply(new BigDecimal(25))).divide(new BigDecimal(100), 9, RoundingMode.CEILING)).add(new BigDecimal(70500));
        } else {
            BigDecimal balance = taxableAmount.subtract(new BigDecimal(1000000));
            BigDecimal a = new BigDecimal(30);
            BigDecimal c = new BigDecimal(100);
            BigDecimal d = new BigDecimal(130500);
            BigDecimal e = (a.multiply(balance)).divide(c, 9, RoundingMode.CEILING);
            return e.add(d);
        }
    }

    @Override
    public Mono<Void> delete(Long id) {
        return payrollRepository.deleteById(id);
    }

    @Override
    public Mono<Payroll> run(Long id) {
        return payrollRepository.findById(id)
                .flatMap(payroll -> staffRepository.findByCompanyId(payroll.getCompanyId())
                        .flatMap(staff -> processStaffPayroll(staff, payroll)).then(Mono.just(payroll)));
    }

    private Flux<PayrollItem> processStaffPayroll(Staff staff, Payroll payroll) {
        Flux<MiscEarning> miscEarningsFlux = miscEarningService.findByStaffIdAndRecurrentTrue(staff.getId());

        Mono<BigDecimal> totalRecurringEarningsToAddToGross = miscEarningsFlux.filter(MiscEarning::getAddedToGross).map(MiscEarning::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Mono<BigDecimal> totalRecurringEarningsToAddToNet = miscEarningsFlux.filter(miscEarning -> !miscEarning.getAddedToGross()).map(MiscEarning::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return Mono.zip(totalRecurringEarningsToAddToGross, totalRecurringEarningsToAddToNet).flatMapMany(totalEarnings -> {
            BigDecimal adjustedGrossSalary = staff.getSalary().add(totalEarnings.getT1());

            return monthlyDeductionRepository.findByStaffId(staff.getId()).flatMap(monthlyDeduction -> deductionAmount(adjustedGrossSalary, monthlyDeduction.getDeductionId(), payroll.getCompanyId()).flatMap(deductionAmt -> {
                PayrollDeduction payrollDeduction = new PayrollDeduction(payroll.getId(), staff.getId(), monthlyDeduction.getDeductionId(), deductionAmt);
                requestLoanRepayment(payroll.getId(), staff.getId(), staff.getUserId(), monthlyDeduction.getDeductionId());
                return payrollDeductionRepository.save(payrollDeduction);
            })).collectList().flatMapMany(deductions -> {
                BigDecimal totalDeductions = deductions.stream().map(PayrollDeduction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal netSalaryBeforeMisc = adjustedGrossSalary.subtract(totalDeductions);
                BigDecimal finalNetSalary = netSalaryBeforeMisc.add(totalEarnings.getT2());

                PayrollItem payrollItem = new PayrollItem(payroll.getId(), staff.getId(), adjustedGrossSalary, totalDeductions, BigDecimal.ZERO, finalNetSalary);
                return payrollItemRepository.save(payrollItem).flux();
            });
        });
    }

    private void requestLoanRepayment(Long payrollId, Long staffId, Long clientId, Long deductionId) {
        LoanRepaymentRequestDto dto = new LoanRepaymentRequestDto(payrollId, staffId, clientId, deductionId);
        producer.sendToLoanService(dto);
    }


    private Mono<BigDecimal> deductionAmount(BigDecimal grossSalary, Long deductionId, Long companyId) {
        return deductionAccountRepository.findByDeductionIdAndCompanyId(deductionId, companyId).flatMap(deductionAccount -> deductionRepository.findById(deductionAccount.getDeductionId()).map(deduction -> {
            deductionAccount.setDeduction(deduction);
            return deductionAccount;
        })).map(r -> calculateDeduction(r.getDeduction(), grossSalary, r.getFixedAmount(), r.getPercentage())).defaultIfEmpty(BigDecimal.ZERO);
    }

    private BigDecimal calculateDeduction(Deduction deduction, BigDecimal grossSalary, BigDecimal fixedAmount, Double percentage) {
        BigDecimal taxableAmount = taxableAmount(grossSalary, fixedAmount, percentage);
        BigDecimal stockDeduction = (taxableAmount.multiply(new BigDecimal(0.01 * percentage))).add(fixedAmount);
        return switch (deduction.getCode()) {
            case "100" -> this.ssf(grossSalary, fixedAmount, percentage);
            case "101" -> this.hi(grossSalary, fixedAmount, percentage);
            case "102" -> this.payAsYouEarn(taxableAmount);
            case "103", "104", "105", "106" -> stockDeduction;
            default -> BigDecimal.ZERO;
        };
    }

    private BigDecimal loanRepayment(Long clientId) {
        return BigDecimal.ZERO;
    }

    private BigDecimal ssf(BigDecimal grossSalary, BigDecimal fixedAmount, Double percentage) {
        return ((new BigDecimal(0.01 * percentage)).multiply(grossSalary)).add(fixedAmount);
    }

    private BigDecimal hi(BigDecimal grossSalary, BigDecimal fixedAmount, Double percentage) {
        return ((new BigDecimal(0.01 * percentage)).multiply(grossSalary)).add(fixedAmount);
    }

    private BigDecimal taxableAmount(BigDecimal grossSalary, BigDecimal fixedAmount, Double percentage) {
        return grossSalary.subtract(ssf(grossSalary, fixedAmount, percentage).add(hi(grossSalary, fixedAmount, percentage)));
    }
}
