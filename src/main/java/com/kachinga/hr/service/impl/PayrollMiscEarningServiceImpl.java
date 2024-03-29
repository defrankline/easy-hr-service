package com.kachinga.hr.service.impl;

import com.kachinga.hr.repository.PayrollMiscEarningRepository;
import com.kachinga.hr.service.PayrollMiscEarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PayrollMiscEarningServiceImpl implements PayrollMiscEarningService {
    private final PayrollMiscEarningRepository payrollMiscEarningRepository;

    @Override
    public Mono<Void> delete(Long id) {
        return payrollMiscEarningRepository.deleteById(id);
    }
}
