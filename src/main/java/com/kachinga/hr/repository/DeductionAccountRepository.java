package com.kachinga.hr.repository;


import com.kachinga.hr.domain.DeductionAccount;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DeductionAccountRepository extends ReactiveCrudRepository<DeductionAccount, Long> {
    Flux<DeductionAccount> findByDeductionIdAndCompanyId(Long deductionId, Long companyId, PageRequest pageRequest);

    Mono<DeductionAccount> findByDeductionIdAndCompanyId(Long deductionId, Long companyId);

    Mono<Long> countAllByDeductionIdAndCompanyId(Long deductionId, Long companyId);
}
