package com.kachinga.hr.repository;


import com.kachinga.hr.domain.MiscEarningItem;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MiscEarningItemRepository extends ReactiveCrudRepository<MiscEarningItem, Long> {
    Flux<MiscEarningItem> findByCompanyId(Long companyId, PageRequest pageRequest);

    Mono<Long> countAllByCompanyId(Long companyId);

    Mono<MiscEarningItem> findByNameAndCompanyId(String name, Long companyId);
}
