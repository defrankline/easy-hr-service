package com.kachinga.hr.controller;

import com.kachinga.hr.domain.MonthlyDeduction;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.MonthlyDeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/monthly-deductions")
public class MonthlyDeductionController {

    private final MonthlyDeductionService monthlyDeductionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MonthlyDeduction> createStaff(@RequestBody MonthlyDeduction monthlyDeduction) {
        return monthlyDeductionService.save(monthlyDeduction);
    }

    @PutMapping("/{id}")
    public Mono<MonthlyDeduction> updateStaff(@PathVariable Long id, @RequestBody MonthlyDeduction monthlyDeduction) {
        monthlyDeduction.setId(id);
        return monthlyDeductionService.save(monthlyDeduction);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<DataDto<MonthlyDeduction>> getAllDeduction(@RequestParam(name = "sortBy", defaultValue = "amount") String sortBy,
                                                           @RequestParam(name = "staffId") Long staffId,
                                                           @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
                                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                                           @RequestParam(name = "size", defaultValue = "20") int size) {
        return monthlyDeductionService.findAll(staffId, page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    public Mono<MonthlyDeduction> getStaffById(@PathVariable Long id) {
        return monthlyDeductionService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteStaffById(@PathVariable Long id) {
        return monthlyDeductionService.delete(id);
    }
}

