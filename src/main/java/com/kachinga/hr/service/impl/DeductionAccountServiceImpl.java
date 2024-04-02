package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.Deduction;
import com.kachinga.hr.domain.DeductionAccount;
import com.kachinga.hr.domain.Staff;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.domain.dto.DeductionAccountDto;
import com.kachinga.hr.repository.DeductionAccountRepository;
import com.kachinga.hr.repository.DeductionRepository;
import com.kachinga.hr.repository.StaffRepository;
import com.kachinga.hr.service.DeductionAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DeductionAccountServiceImpl implements DeductionAccountService {
    private final DeductionRepository deductionRepository;
    private final DeductionAccountRepository deductionAccountRepository;
    private final StaffRepository staffRepository;

    public Mono<DeductionAccount> create(DeductionAccount deductionAccount) {
        deductionAccount.setId(null);
        return deductionAccountRepository.save(deductionAccount);
    }

    public Mono<DeductionAccount> update(Long id, DeductionAccount deductionAccount) {
        return deductionAccountRepository.findById(id).flatMap(existingDeduction -> {
            existingDeduction.setDeductionId(deductionAccount.getDeductionId());
            existingDeduction.setAccountId(deductionAccount.getAccountId());
            existingDeduction.setFixedAmount(deductionAccount.getFixedAmount());
            existingDeduction.setPercentage(deductionAccount.getPercentage());
            return deductionAccountRepository.save(existingDeduction);
        }).switchIfEmpty(Mono.error(new RuntimeException("DeductionAccount not found with id " + deductionAccount.getId())));
    }

    @Override
    public Mono<DeductionAccountDto> getById(Long id) {
        return deductionAccountRepository.findById(id).flatMap(deductionAccount -> deductionRepository.findById(deductionAccount.getDeductionId()).map(deduction -> new DeductionAccountDto(deductionAccount.getId(), deductionAccount.getAccountId(), deductionAccount.getDeductionId(), deductionAccount.getPercentage(), deductionAccount.getFixedAmount(), deduction)));
    }

    @Override
    public Mono<DeductionAccountDto> findByDeductionIdAndCompanyId(Long deductionId, Long companyId) {
        return deductionAccountRepository.findByDeductionIdAndCompanyId(deductionId, companyId).flatMap(deductionAccount -> deductionRepository.findById(deductionAccount.getDeductionId()).map(deduction -> new DeductionAccountDto(deductionAccount.getId(), deductionAccount.getAccountId(), deductionAccount.getDeductionId(), deductionAccount.getPercentage(), deductionAccount.getFixedAmount(), deduction)));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return deductionAccountRepository.deleteById(id);
    }

    private Flux<DeductionAccountDto> findAllWithDetails(Long companyId, PageRequest pageRequest) {
        return deductionAccountRepository.findByCompanyId(companyId, pageRequest).flatMap(deductionAccount -> Mono.zip(Mono.just(deductionAccount), deductionRepository.findById(deductionAccount.getDeductionId())).map(tuple -> {
            DeductionAccount deductionAccountDetails = tuple.getT1();
            Deduction deductionDetails = tuple.getT2();
            return new DeductionAccountDto(deductionAccountDetails.getId(), deductionAccountDetails.getAccountId(), deductionAccountDetails.getDeductionId(), deductionAccountDetails.getPercentage(), deductionAccountDetails.getFixedAmount(), deductionDetails);
        }));
    }


    @Override
    public Mono<DataDto<DeductionAccountDto>> findAll(Long companyId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Flux<DeductionAccountDto> deductionsFlux = findAllWithDetails(companyId, pageRequest);
        Mono<Long> countMono = deductionAccountRepository.countAllByCompanyId(companyId);
        return Mono.zip(deductionsFlux.collectList(), countMono, DataDto::new);
    }

    @Override
    public Mono<BigDecimal> deductionAmount(Long staffId, Long deductionId, Long companyId) {
        Mono<Staff> staffMono = staffRepository.findById(staffId);
        Mono<DeductionAccountDto> deductionAccountMono = this.findByDeductionIdAndCompanyId(deductionId, companyId);

        return Mono.zip(staffMono, deductionAccountMono).flatMap(tuple -> {
            Staff staff = tuple.getT1();
            DeductionAccountDto deductionAccount = tuple.getT2();

            BigDecimal grossSalary = staff.getSalary();
            BigDecimal fixedAmount = deductionAccount.getFixedAmount() != null ? deductionAccount.getFixedAmount() : BigDecimal.ZERO;
            Double percentage = deductionAccount.getPercentage() != null ? deductionAccount.getPercentage() : 0.0;

            BigDecimal taxableAmount = taxableAmount(grossSalary, fixedAmount, percentage);
            BigDecimal stockDeduction = taxableAmount.multiply(BigDecimal.valueOf(0.01)).multiply(new BigDecimal(percentage)).add(fixedAmount);

            return Mono.just(switch (deductionAccount.getDeduction().getCode()) {
                case "100" -> ssf(grossSalary, fixedAmount, percentage);
                case "101" -> hi(grossSalary, fixedAmount, percentage);
                case "102" -> payAsYouEarn(taxableAmount);
                case "103", "104", "105", "106" -> stockDeduction;
                case "108" -> studentLoan(grossSalary, fixedAmount, percentage);
                default -> BigDecimal.ZERO;
            });
        });
    }

    private BigDecimal payAsYouEarn(BigDecimal taxableAmount) {
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal threshold1 = new BigDecimal("270000");
        BigDecimal threshold2 = new BigDecimal("520000");
        BigDecimal threshold3 = new BigDecimal("760000");
        BigDecimal threshold4 = new BigDecimal("1000000");
        BigDecimal rate1 = new BigDecimal("0.08");
        BigDecimal rate2 = new BigDecimal("0.20");
        BigDecimal rate3 = new BigDecimal("0.25");
        BigDecimal rate4 = new BigDecimal("0.30");
        BigDecimal fixedAmount1 = new BigDecimal("20000");
        BigDecimal fixedAmount2 = new BigDecimal("68000");
        BigDecimal fixedAmount3 = new BigDecimal("128000");

        if (taxableAmount.compareTo(threshold1) <= 0) {
            return zero;
        } else if (taxableAmount.compareTo(threshold1) > 0 && taxableAmount.compareTo(threshold2) <= 0) {
            return taxableAmount.subtract(threshold1).multiply(rate1);
        } else if (taxableAmount.compareTo(threshold2) > 0 && taxableAmount.compareTo(threshold3) <= 0) {
            return fixedAmount1.add(taxableAmount.subtract(threshold2).multiply(rate2));
        } else if (taxableAmount.compareTo(threshold3) > 0 && taxableAmount.compareTo(threshold4) <= 0) {
            return fixedAmount2.add(taxableAmount.subtract(threshold3).multiply(rate3));
        } else if (taxableAmount.compareTo(threshold4) > 0) {
            return fixedAmount3.add(taxableAmount.subtract(threshold4).multiply(rate4));
        }

        return zero;
    }

    private BigDecimal ssf(BigDecimal grossSalary, BigDecimal fixedAmount, Double percentage) {
        return ((new BigDecimal(0.01 * percentage)).multiply(grossSalary)).add(fixedAmount);
    }

    private BigDecimal studentLoan(BigDecimal grossSalary, BigDecimal fixedAmount, Double percentage) {
        return ((new BigDecimal(0.01 * percentage)).multiply(grossSalary)).add(fixedAmount);
    }

    private BigDecimal hi(BigDecimal grossSalary, BigDecimal fixedAmount, Double percentage) {
        return ((new BigDecimal(0.01 * percentage)).multiply(grossSalary)).add(fixedAmount);
    }

    private BigDecimal taxableAmount(BigDecimal grossSalary, BigDecimal fixedAmount, Double percentage) {
        BigDecimal ssf = ssf(grossSalary, fixedAmount, percentage);
        BigDecimal hi = hi(grossSalary, fixedAmount, percentage);
        return grossSalary.subtract(ssf);
    }
}
