package com.kachinga.hr.service;

import com.kachinga.hr.domain.Staff;
import com.kachinga.hr.domain.dto.DataDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface StaffService {
    Mono<Staff> create(Staff staff);

    Mono<Staff> update(Long id, Staff staff);

    Mono<Staff> getById(Long id);

    Mono<DataDto<Staff>> findAll(Long companyId, int page, int size, String sortBy, String sortDirection);

    Mono<Void> delete(Long id);
}
