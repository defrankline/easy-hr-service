package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.DeductionAccount;
import com.kachinga.hr.domain.Staff;
import com.kachinga.hr.domain.dto.DataDto;
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

    @Override
    public Mono<DeductionAccount> save(DeductionAccount deductionAccount) {
        return this.findByDeductionIdAndCompanyId(deductionAccount.getDeductionId(), deductionAccount.getCompanyId())
                .flatMap(existingDeduction -> {
                    deductionAccount.setDeductionId(deductionAccount.getDeductionId());
                    deductionAccount.setAccountId(deductionAccount.getAccountId());
                    deductionAccount.setFixedAmount(deductionAccount.getFixedAmount());
                    deductionAccount.setPercentage(deductionAccount.getPercentage());
                    deductionAccount.setId(existingDeduction.getId());
                    return deductionAccountRepository.save(deductionAccount);
                })
                .switchIfEmpty(Mono.defer(() -> deductionAccountRepository.save(deductionAccount)));
    }

    @Override
    public Mono<DeductionAccount> getById(Long id) {
        return deductionAccountRepository.findById(id)
                .flatMap(deductionAccount -> {
                    if (deductionAccount.getDeductionId() != null) {
                        return deductionRepository.findById(deductionAccount.getDeductionId())
                                .map(deduction -> {
                                    deductionAccount.setDeduction(deduction);
                                    return deductionAccount;
                                });
                    } else {
                        return Mono.just(deductionAccount);
                    }
                });
    }

    @Override
    public Mono<DeductionAccount> findByDeductionIdAndCompanyId(Long deductionId, Long companyId) {
        return deductionAccountRepository.findByDeductionIdAndCompanyId(deductionId, companyId)
                .flatMap(deductionAccount -> {
                    if (deductionAccount.getDeductionId() != null) {
                        return deductionRepository.findById(deductionAccount.getDeductionId())
                                .map(deduction -> {
                                    deductionAccount.setDeduction(deduction);
                                    return deductionAccount;
                                });
                    } else {
                        return Mono.just(deductionAccount);
                    }
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return deductionAccountRepository.deleteById(id);
    }

    private Flux<DeductionAccount> findAllWithDetails(Long deductionId, Long companyId, PageRequest pageRequest) {
        return deductionAccountRepository.findByDeductionIdAndCompanyId(deductionId, companyId, pageRequest)
                .flatMap(deductionAccount -> Mono.zip(
                        Mono.just(deductionAccount),
                        deductionRepository.findById(deductionAccount.getDeductionId())
                ).map(tuple -> {
                    deductionAccount.setDeduction(tuple.getT2());
                    return deductionAccount;
                }));
    }

    @Override
    public Mono<DataDto<DeductionAccount>> findAll(Long deductionId, Long companyId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Flux<DeductionAccount> deductionsFlux = findAllWithDetails(deductionId, companyId, pageRequest);
        Mono<Long> countMono = deductionAccountRepository.countAllByDeductionIdAndCompanyId(deductionId, companyId);
        return Mono.zip(deductionsFlux.collectList(), countMono, DataDto::new);
    }

    @Override
    public Mono<BigDecimal> deductionAmount(Long staffId, Long deductionId, Long companyId) {
        Mono<Staff> staffMono = staffRepository.findById(staffId);
        Mono<DeductionAccount> deductionAccountMono = this.findByDeductionIdAndCompanyId(deductionId, companyId);

        return Mono.zip(staffMono, deductionAccountMono)
                .flatMap(tuple -> {
                    Staff staff = tuple.getT1();
                    DeductionAccount deductionAccount = tuple.getT2();

                    BigDecimal grossSalary = staff.getSalary();
                    BigDecimal fixedAmount = deductionAccount.getFixedAmount();
                    Double percentage = deductionAccount.getPercentage();

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
