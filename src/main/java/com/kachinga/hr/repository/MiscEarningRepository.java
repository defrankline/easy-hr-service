package com.kachinga.hr.repository;

import com.kachinga.hr.domain.MiscEarning;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface MiscEarningRepository extends ReactiveCrudRepository<MiscEarning, Long> {
    Flux<MiscEarning> findByStaffIdAndRecurrentTrue(Long staffId);
}

