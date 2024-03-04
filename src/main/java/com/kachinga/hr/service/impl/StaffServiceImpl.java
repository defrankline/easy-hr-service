package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.Staff;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.repository.StaffRepository;
import com.kachinga.hr.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;

    @Override
    public Mono<Staff> create(Staff staff) {
        return staffRepository.findByNumberAndCompanyId(staff.getNumber(), staff.getCompanyId())
                .hasElement()
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Number and CompanyId combination must be unique"));
                    } else {
                        return staffRepository.findByUserIdAndCompanyId(staff.getUserId(), staff.getCompanyId()).hasElement();
                    }
                })
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "UserId and CompanyId combination must be unique"));
                    } else {
                        return staffRepository.save(staff);
                    }
                });
    }

    @Override
    public Mono<Staff> update(Long id, Staff updatedStaff) {
        return staffRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Staff not found")))
                .flatMap(existingStaff ->
                        staffRepository.findByNumberAndCompanyId(updatedStaff.getNumber(), updatedStaff.getCompanyId())
                                .filter(dbStaff -> !dbStaff.getId().equals(id))
                                .flatMap(dbStaff -> Mono.<Staff>error(new RuntimeException("Number and CompanyId combination must be unique")))
                                .switchIfEmpty(staffRepository.findByUserIdAndCompanyId(updatedStaff.getUserId(), updatedStaff.getCompanyId())
                                        .filter(dbStaff -> !dbStaff.getId().equals(id))
                                        .flatMap(dbStaff -> Mono.<Staff>error(new RuntimeException("UserId and CompanyId combination must be unique")))
                                        .then(Mono.just(existingStaff)))
                ).flatMap(existingStaff -> {
                    existingStaff.setNumber(updatedStaff.getNumber());
                    existingStaff.setUserId(updatedStaff.getUserId());
                    existingStaff.setCompanyId(updatedStaff.getCompanyId());
                    existingStaff.setSalary(updatedStaff.getSalary());
                    existingStaff.setTitle(updatedStaff.getTitle());
                    return staffRepository.save(existingStaff);
                });
    }

    @Override
    public Mono<Staff> getById(Long id) {
        return staffRepository.findById(id);
    }

    public Mono<DataDto<Staff>> findAll(Long companyId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Mono<List<Staff>> items = staffRepository.findByCompanyId(companyId, pageRequest).collectList();
        Mono<Long> countMono = staffRepository.count();
        return Mono.zip(items, countMono, DataDto::new);
    }


    @Override
    public Mono<Void> delete(Long id) {
        return staffRepository.deleteById(id);
    }
}
