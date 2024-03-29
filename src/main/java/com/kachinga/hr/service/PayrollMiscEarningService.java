package com.kachinga.hr.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface PayrollMiscEarningService {
    Mono<Void> delete(Long id);
}
