package com.kachinga.hr.repository;


import com.kachinga.hr.domain.Deduction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DeductionRepository extends ReactiveCrudRepository<Deduction, Long> {
    Mono<Deduction> findByCode(String code);

    Flux<Deduction> findAll(Sort sort);


    Flux<Deduction> findAllBy(PageRequest pageRequest);

    @Query("SELECT * FROM deductions d WHERE LOWER(d.name) ILIKE LOWER(:searchTerm) OR d.code ILIKE :searchTerm")
    Flux<Deduction> findAllBy(@Param("searchTerm") String searchTerm, Sort sort);

    @Query("SELECT * FROM deductions d WHERE LOWER(d.name) ILIKE LOWER(:searchTerm) OR d.code ILIKE :searchTerm")
    Flux<Deduction> findAllBy(@Param("searchTerm") String searchTerm, PageRequest pageRequest);

    Mono<Long> count();
}
