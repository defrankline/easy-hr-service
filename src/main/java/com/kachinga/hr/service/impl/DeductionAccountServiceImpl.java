package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.DeductionAccount;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.repository.DeductionAccountRepository;
import com.kachinga.hr.repository.DeductionRepository;
import com.kachinga.hr.service.DeductionAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeductionAccountServiceImpl implements DeductionAccountService {
    private final DeductionRepository deductionRepository;
    private final DeductionAccountRepository deductionAccountRepository;

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
        return deductionAccountRepository.findById(id);
    }

    @Override
    public Mono<DeductionAccount> findByDeductionIdAndCompanyId(Long deductionId, Long companyId) {
        return deductionAccountRepository.findByDeductionIdAndCompanyId(deductionId, companyId);
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
}
