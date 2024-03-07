package com.kachinga.hr.repository;


import com.kachinga.hr.domain.Staff;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StaffRepository extends ReactiveCrudRepository<Staff, Long> {
    Flux<Staff> findByCompanyId(Long companyId);

    Flux<Staff> findByCompanyId(Long companyId, PageRequest pageRequest);

    Mono<Staff> findByNumberAndCompanyId(String number, Long companyId);

    Mono<Staff> findByUserIdAndCompanyId(Long userId, Long companyId);

    Mono<Long> countByCompanyId(Long companyId);
}
