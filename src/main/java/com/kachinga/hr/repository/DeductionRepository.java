package com.kachinga.hr.repository;


import com.kachinga.hr.domain.Deduction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DeductionRepository extends ReactiveCrudRepository<Deduction, Long> {
    Mono<Deduction> findByCode(String code);

    Flux<Deduction> findAll(Sort sort);

    Flux<Deduction> findAllBy(PageRequest pageRequest);

    Mono<Long> count();
}
