package com.kachinga.hr.service;

import com.kachinga.hr.domain.Deduction;
import com.kachinga.hr.domain.dto.DataDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface DeductionService {
    Mono<Deduction> save(Deduction deduction);

    Mono<Deduction> getById(Long id);

    Mono<Deduction> getByCode(String code);

    Mono<DataDto<Deduction>> findAll(int page, int size, String sortBy, String sortDirection);

    Mono<Void> delete(Long id);
}
