package com.kachinga.hr.repository;


import com.kachinga.hr.domain.Staff;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface StaffRepository extends ReactiveCrudRepository<Staff, Long> {
    Flux<Staff> findByCompanyId(Long companyId, Sort sort);

    Flux<Staff> findByCompanyId(Long companyId, Pageable pageable);
}
