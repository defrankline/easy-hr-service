package com.kachinga.hr.repository;


import com.kachinga.hr.domain.PayrollDeduction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PayrollDeductionRepository extends ReactiveCrudRepository<PayrollDeduction, Long> {
    Flux<PayrollDeduction> findByStaffId(Long staffId, PageRequest pageRequest);

    Mono<PayrollDeduction> findByPayrollIdAndStaffIdAndDeductionId(Long payrollId, Long staffId, Long deductionId);

    Mono<Long> countAllByStaffId(Long staffId);

    Mono<Void> deleteByPayrollId(Long payrollId);
}
