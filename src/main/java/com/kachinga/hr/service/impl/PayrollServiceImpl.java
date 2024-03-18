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
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {
    private final PayrollRepository payrollRepository;
    private final MonthlyDeductionRepository monthlyDeductionRepository;
    private final PayrollDeductionRepository payrollDeductionRepository;
    private final PayrollItemRepository payrollItemRepository;
    private final StaffRepository staffRepository;
    private final MiscEarningService miscEarningService;
    private final Producer producer;

    @Override
    public Mono<Payroll> save(Payroll payroll) {
        return payrollRepository.findById(payroll.getId())
                .flatMap(existingDeduction -> {
                    payroll.setName(payroll.getName());
                    payroll.setCode(payroll.getCode());
                    payroll.setDate(payroll.getDate());
                    payroll.setApproved(payroll.getApproved());
                    payroll.setCompanyId(payroll.getCompanyId());
                    payroll.setSalaryExpenseAccountId(payroll.getSalaryExpenseAccountId());
                    payroll.setId(existingDeduction.getId());
                    return payrollRepository.save(payroll).flatMap(savedPayroll -> run(savedPayroll.getId()));
                })
                .switchIfEmpty(Mono.defer(() -> payrollRepository.save(payroll)))
                .flatMap(savedPayroll -> run(savedPayroll.getId()));
    }

    @Override
    public Mono<Payroll> run(Long id) {
        return payrollRepository.findById(id)
                .flatMap(payroll -> staffRepository.findByCompanyId(payroll.getCompanyId())
                        .flatMap(staff -> processStaffPayroll(staff, payroll)).then(Mono.just(payroll)));
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

    @Override
    public Mono<Void> delete(Long id) {
        return payrollRepository.deleteById(id);
    }

    private Flux<PayrollItem> processStaffPayroll(Staff staff, Payroll payroll) {
        Flux<MiscEarning> miscEarningsFlux = miscEarningService.findByStaffIdAndRecurrentTrue(staff.getId());

        Mono<BigDecimal> totalRecurringEarningsToAddToGross = miscEarningsFlux.filter(MiscEarning::getAddedToGross).map(MiscEarning::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Mono<BigDecimal> totalRecurringEarningsToAddToNet = miscEarningsFlux.filter(miscEarning -> !miscEarning.getAddedToGross()).map(MiscEarning::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return Mono.zip(totalRecurringEarningsToAddToGross, totalRecurringEarningsToAddToNet).flatMapMany(totalEarnings -> {
            BigDecimal adjustedGrossSalary = staff.getSalary().add(totalEarnings.getT1());

            return monthlyDeductionRepository.findByStaffId(staff.getId()).flatMap(monthlyDeduction -> {
                PayrollDeduction payrollDeduction = new PayrollDeduction(payroll.getId(), staff.getId(), monthlyDeduction.getDeductionId(), monthlyDeduction.getAmount());
                requestLoanRepayment(payroll.getId(), staff.getId(), staff.getUserId(), monthlyDeduction.getDeductionId());
                return payrollDeductionRepository.save(payrollDeduction);
            }).collectList().flatMapMany(deductions -> {
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
}
