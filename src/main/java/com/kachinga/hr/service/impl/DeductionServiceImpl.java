package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.Deduction;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.repository.DeductionRepository;
import com.kachinga.hr.service.DeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionServiceImpl implements DeductionService {
    private final DeductionRepository deductionRepository;

    @Override
    public Mono<Deduction> save(Deduction deduction) {
        return deductionRepository.save(deduction);
    }

    @Override
    public Mono<Deduction> getById(Long id) {
        return deductionRepository.findById(id);
    }

    @Override
    public Mono<Deduction> getByCode(String code) {
        return deductionRepository.findByCode(code);
    }

    public Mono<DataDto<Deduction>> findAll(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Mono<List<Deduction>> deductionsMono = deductionRepository.findAllBy(pageRequest).collectList();
        Mono<Long> countMono = deductionRepository.count();
        return Mono.zip(deductionsMono, countMono, DataDto::new);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return deductionRepository.deleteById(id);
    }
}
