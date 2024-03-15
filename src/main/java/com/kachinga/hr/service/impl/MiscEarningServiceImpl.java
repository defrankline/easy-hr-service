package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.MiscEarning;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.repository.MiscEarningRepository;
import com.kachinga.hr.service.MiscEarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        return miscEarningRepository.findByStaffIdAndRecurrentTrue(staffId).map(MiscEarning::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Mono<DataDto<MiscEarning>> findAll(Long staffId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Flux<MiscEarning> deductionsFlux = miscEarningRepository.findByStaffId(staffId, pageRequest);
        Mono<Long> countMono = miscEarningRepository.countAllByStaffId(staffId);
        return Mono.zip(deductionsFlux.collectList(), countMono, DataDto::new);
    }

    @Override
    public Mono<MiscEarning> create(MiscEarning miscEarning) {
        return miscEarningRepository.save(miscEarning);
    }

    @Override
    public Mono<MiscEarning> update(Long id, MiscEarning miscEarning) {
        return miscEarningRepository.findById(id).flatMap(existingMiscEarning -> {
            existingMiscEarning.setItem(miscEarning.getItem());
            existingMiscEarning.setAmount(miscEarning.getAmount());
            existingMiscEarning.setRecurrent(miscEarning.getRecurrent());
            existingMiscEarning.setAddedToGross(miscEarning.getAddedToGross());
            return miscEarningRepository.save(existingMiscEarning);
        });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return miscEarningRepository.deleteById(id);
    }

    @Override
    public Mono<MiscEarning> getById(Long id) {
        return miscEarningRepository.findById(id);
    }
}
