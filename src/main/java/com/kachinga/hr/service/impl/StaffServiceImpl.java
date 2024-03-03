package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.Staff;
import com.kachinga.hr.repository.StaffRepository;
import com.kachinga.hr.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;

    @Override
    public Mono<Staff> save(Staff staff) {
        return staffRepository.save(staff);
    }

    @Override
    public Mono<Staff> getById(Long id) {
        return staffRepository.findById(id);
    }

    @Override
    public Flux<Staff> getAll(Long companyId, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return staffRepository.findByCompanyId(companyId, sort);
    }

    @Override
    public Flux<Staff> getAll(Long companyId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return staffRepository.findByCompanyId(companyId, PageRequest.of(page, size, sort));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return staffRepository.deleteById(id);
    }
}
