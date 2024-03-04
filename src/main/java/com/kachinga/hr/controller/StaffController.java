package com.kachinga.hr.controller;

import com.kachinga.hr.domain.Staff;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/staff")
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Staff> createStaff(@RequestBody Staff staff) {
        return staffService.create(staff);
    }

    @PutMapping("/{id}")
    public Mono<Staff> updateStaff(@PathVariable Long id, @RequestBody Staff staff) {
        return staffService.update(id, staff);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<DataDto<Staff>> getAllDeduction(@RequestParam(name = "sortBy", defaultValue = "number") String sortBy,
                                                @RequestParam(name = "companyId") Long companyId,
                                                @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
                                                @RequestParam(name = "page", defaultValue = "0") int page,
                                                @RequestParam(name = "size", defaultValue = "20") int size) {
        return staffService.findAll(companyId, page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    public Mono<Staff> getStaffById(@PathVariable Long id) {
        return staffService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteStaffById(@PathVariable Long id) {
        return staffService.delete(id);
    }
}

