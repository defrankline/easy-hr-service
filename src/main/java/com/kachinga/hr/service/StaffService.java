package com.kachinga.hr.service;

import com.kachinga.hr.domain.Staff;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface StaffService {
    Mono<Staff> save(Staff staff);

    Mono<Staff> getById(Long id);

    Flux<Staff> getAll(Long companyId, String sortBy, String sortDirection);

    Flux<Staff> getAll(Long companyId, int page, int size, String sortBy, String sortDirection);

    Mono<Void> delete(Long id);
}
