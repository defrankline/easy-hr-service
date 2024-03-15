package com.kachinga.hr.repository;

import com.kachinga.hr.domain.MiscEarning;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface MiscEarningRepository extends ReactiveCrudRepository<MiscEarning, Long> {
    Flux<MiscEarning> findByStaffIdAndRecurrentTrue(Long staffId);

    Flux<MiscEarning> findByStaffId(Long staffId, PageRequest pageRequest);

    Mono<Long> countAllByStaffId(Long staffId);
}

