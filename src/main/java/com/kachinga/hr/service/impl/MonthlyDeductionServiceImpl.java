package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.MonthlyDeduction;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.repository.DeductionRepository;
import com.kachinga.hr.repository.MonthlyDeductionRepository;
import com.kachinga.hr.repository.StaffRepository;
import com.kachinga.hr.service.MonthlyDeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MonthlyDeductionServiceImpl implements MonthlyDeductionService {
    private final StaffRepository staffRepository;
    private final DeductionRepository deductionRepository;
    private final MonthlyDeductionRepository monthlyDeductionRepository;

    @Override
    public Mono<MonthlyDeduction> save(MonthlyDeduction monthlyDeduction) {
        return monthlyDeductionRepository.findByStaffIdAndDeductionId(monthlyDeduction.getStaffId(), monthlyDeduction.getDeductionId())
                .flatMap(existingDeduction -> {
                    monthlyDeduction.setDeductionId(monthlyDeduction.getDeductionId());
                    monthlyDeduction.setStaffId(monthlyDeduction.getStaffId());
                    monthlyDeduction.setId(existingDeduction.getId());
                    return monthlyDeductionRepository.save(monthlyDeduction);
                })
                .switchIfEmpty(Mono.defer(() -> monthlyDeductionRepository.save(monthlyDeduction)));
    }

    @Override
    public Mono<MonthlyDeduction> getById(Long id) {
        return monthlyDeductionRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return monthlyDeductionRepository.deleteById(id);
    }

    private Flux<MonthlyDeduction> findAllWithDetails(Long staffId, PageRequest pageRequest) {
        return monthlyDeductionRepository.findByStaffId(staffId, pageRequest)
                .flatMap(monthlyDeduction -> Mono.zip(
                        Mono.just(monthlyDeduction),
                        staffRepository.findById(monthlyDeduction.getStaffId()),
                        deductionRepository.findById(monthlyDeduction.getDeductionId())
                ).map(tuple -> {
                    monthlyDeduction.setStaff(tuple.getT2());
                    monthlyDeduction.setDeduction(tuple.getT3());
                    return monthlyDeduction;
                }));
    }

    @Override
    public Mono<DataDto<MonthlyDeduction>> findAll(Long staffId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Flux<MonthlyDeduction> deductionsFlux = findAllWithDetails(staffId, pageRequest);
        Mono<Long> countMono = monthlyDeductionRepository.countAllByStaffId(staffId);
        return Mono.zip(deductionsFlux.collectList(), countMono, DataDto::new);
    }
}
