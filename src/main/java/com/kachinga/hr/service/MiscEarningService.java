package com.kachinga.hr.service;

import com.kachinga.hr.domain.MiscEarning;
import com.kachinga.hr.domain.dto.DataDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public interface MiscEarningService {
    Flux<MiscEarning> findByStaffIdAndRecurrentTrue(Long staffId);

    Mono<BigDecimal> getTotalRecurringEarningsByStaffId(Long staffId);

    Mono<DataDto<MiscEarning>> findAll(Long staffId, int page, int size, String sortBy, String sortDirection);

    Mono<MiscEarning> create(MiscEarning miscEarning);

    Mono<MiscEarning> update(Long id, MiscEarning miscEarning);

    Mono<Void> delete(Long id);

    Mono<MiscEarning> getById(Long id);
}
