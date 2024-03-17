package com.kachinga.hr.service;

import com.kachinga.hr.domain.MiscEarningItem;
import com.kachinga.hr.domain.dto.DataDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public interface MiscEarningItemService {
    Mono<MiscEarningItem> create(MiscEarningItem miscEarningItem);

    Mono<MiscEarningItem> update(Long id, MiscEarningItem miscEarningItem);

    Mono<MiscEarningItem> getById(Long id);

    Mono<MiscEarningItem> findByNameAndCompanyId(String name, Long companyId);

    Mono<DataDto<MiscEarningItem>> findAll(Long companyId, int page, int size, String sortBy, String sortDirection);

    Mono<Void> delete(Long id);

    Mono<BigDecimal> earningAmount(Long staffId, Long itemId);
}
