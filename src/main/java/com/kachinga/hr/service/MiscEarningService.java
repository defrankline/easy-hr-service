package com.kachinga.hr.service;

import com.kachinga.hr.domain.MiscEarning;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public interface MiscEarningService {
    Flux<MiscEarning> findByStaffIdAndRecurrentTrue(Long staffId);
    Mono<BigDecimal> getTotalRecurringEarningsByStaffId(Long staffId);
}
