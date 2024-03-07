package com.kachinga.hr.repository;


import com.kachinga.hr.domain.PayrollItem;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PayrollItemRepository extends ReactiveCrudRepository<PayrollItem, Long> {
    Flux<PayrollItem> findByPayrollId(Long payrollId, PageRequest pageRequest);

    Mono<PayrollItem> findByPayrollIdAndStaffId(Long payrollId, Long staffId);

    Mono<Long> countAllByPayrollId(Long payrollId);
}
