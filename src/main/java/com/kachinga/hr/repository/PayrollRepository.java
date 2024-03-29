package com.kachinga.hr.repository;


import com.kachinga.hr.domain.Payroll;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PayrollRepository extends ReactiveCrudRepository<Payroll, Long> {
    Flux<Payroll> findByCompanyId(Long companyId, PageRequest pageRequest);

    Mono<Long> countByCompanyId(Long companyId);
}
