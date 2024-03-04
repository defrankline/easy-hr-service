package com.kachinga.hr.service;

import com.kachinga.hr.domain.PayrollDeduction;
import com.kachinga.hr.domain.dto.DataDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface PayrollDeductionService {
    Mono<PayrollDeduction> save(PayrollDeduction payrollDeduction);

    Mono<PayrollDeduction> getById(Long id);

    Mono<DataDto<PayrollDeduction>> findAll(Long staffId, int page, int size, String sortBy, String sortDirection);

    Mono<Void> delete(Long id);
}
