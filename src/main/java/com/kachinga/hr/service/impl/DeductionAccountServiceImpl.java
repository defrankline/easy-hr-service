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
import java.math.RoundingMode;

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
                default -> BigDecimal.ZERO;
            });
        });
    }

    private BigDecimal payAsYouEarn(BigDecimal taxableAmount) {
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
