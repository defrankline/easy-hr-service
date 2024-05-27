package com.kachinga.hr.repository;


import com.kachinga.hr.domain.PayrollMiscEarning;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PayrollMiscEarningRepository extends ReactiveCrudRepository<PayrollMiscEarning, Long> {
    Mono<Void> deleteByPayrollId(Long payrollId);
}
