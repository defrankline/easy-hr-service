package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.MiscEarning;
import com.kachinga.hr.repository.MiscEarningRepository;
import com.kachinga.hr.service.MiscEarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MiscEarningServiceImpl implements MiscEarningService {
    private final MiscEarningRepository miscEarningRepository;

    public Flux<MiscEarning> findByStaffIdAndRecurrentTrue(Long staffId) {
        return miscEarningRepository.findByStaffIdAndRecurrentTrue(staffId);
    }

    public Mono<BigDecimal> getTotalRecurringEarningsByStaffId(Long staffId) {
        return miscEarningRepository.findByStaffIdAndRecurrentTrue(staffId)
                .map(MiscEarning::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
