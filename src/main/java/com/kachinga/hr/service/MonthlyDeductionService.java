package com.kachinga.hr.service;

import com.kachinga.hr.domain.MonthlyDeduction;
import com.kachinga.hr.domain.dto.DataDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface MonthlyDeductionService {
    Mono<MonthlyDeduction> save(MonthlyDeduction monthlyDeduction);

    Mono<MonthlyDeduction> getById(Long id);

    Mono<DataDto<MonthlyDeduction>> findAll(Long staffId, int page, int size, String sortBy, String sortDirection);

    Mono<Void> delete(Long id);
}
