package com.kachinga.hr.service;

import com.kachinga.hr.domain.DeductionAccount;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.domain.dto.DeductionAccountDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public interface DeductionAccountService {
    Mono<DeductionAccount> create(DeductionAccount deductionAccount);

    Mono<DeductionAccount> update(Long id, DeductionAccount deductionAccount);

    Mono<DeductionAccountDto> getById(Long id);

    Mono<DeductionAccountDto> findByDeductionIdAndCompanyId(Long deductionId, Long companyId);

    Mono<DataDto<DeductionAccountDto>> findAll(Long companyId, int page, int size, String sortBy, String sortDirection);

    Mono<Void> delete(Long id);

    Mono<BigDecimal> deductionAmount(Long staffId, Long deductionId, Long companyId);
}
