package com.kachinga.hr.service;

import com.kachinga.hr.domain.PayrollItem;
import com.kachinga.hr.domain.dto.DataDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface PayrollItemService {
    Mono<PayrollItem> save(PayrollItem payrollItem);

    Mono<PayrollItem> getById(Long id);

    Mono<DataDto<PayrollItem>> findAll(Long payrollId, int page, int size, String sortBy, String sortDirection);

    Mono<Void> delete(Long id);

    Flux<PayrollItem> save(List<PayrollItem> items);
}
