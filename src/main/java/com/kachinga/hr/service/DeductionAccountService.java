package com.kachinga.hr.service;

import com.kachinga.hr.domain.DeductionAccount;
import com.kachinga.hr.domain.dto.DataDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface DeductionAccountService {
    Mono<DeductionAccount> save(DeductionAccount deductionAccount);

    Mono<DeductionAccount> getById(Long id);

    Mono<DeductionAccount> findByDeductionIdAndCompanyId(Long deductionId, Long companyId);

    Mono<DataDto<DeductionAccount>> findAll(Long deductionId, Long companyId, int page, int size, String sortBy, String sortDirection);

    Mono<Void> delete(Long id);
}
