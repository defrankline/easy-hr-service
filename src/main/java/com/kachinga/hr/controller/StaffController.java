package com.kachinga.hr.controller;

import com.kachinga.hr.domain.Staff;
import com.kachinga.hr.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/staff")
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Staff> saveStaff(@RequestBody Staff staff) {
        return staffService.save(staff);
    }

    @GetMapping
    public Flux<Staff> getAllStaff(@RequestParam(defaultValue = "number") String sortBy,
                                   @RequestParam(defaultValue = "ASC") String sortDirection,
                                   @RequestParam(name = "companyId") Long companyId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        if (size <= 0) {
            return staffService.getAll(companyId, sortBy, sortDirection);
        } else {
            return staffService.getAll(companyId, page, size, sortBy, sortDirection);
        }
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

