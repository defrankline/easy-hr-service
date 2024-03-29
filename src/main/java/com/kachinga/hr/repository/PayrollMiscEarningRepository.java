package com.kachinga.hr.repository;


import com.kachinga.hr.domain.PayrollMiscEarning;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollMiscEarningRepository extends ReactiveCrudRepository<PayrollMiscEarning, Long> {

}
