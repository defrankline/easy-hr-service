package com.kachinga.hr.repository;


import com.kachinga.hr.domain.MonthlyDeduction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MonthlyDeductionRepository extends ReactiveCrudRepository<MonthlyDeduction, Long> {
    Flux<MonthlyDeduction> findByStaffId(Long staffId);

    Flux<MonthlyDeduction> findByStaffId(Long staffId, PageRequest pageRequest);

    Mono<MonthlyDeduction> findByStaffIdAndDeductionId(Long staffId, Long deductionId);

    Mono<Long> countAllByStaffId(Long staffId);
}
