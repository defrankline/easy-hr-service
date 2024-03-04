package com.kachinga.hr.controller;

import com.kachinga.hr.domain.PayrollDeduction;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.PayrollDeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/staff-deductions")
public class PayrollDeductionController {

    private final PayrollDeductionService payrollDeductionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PayrollDeduction> createStaff(@RequestBody PayrollDeduction payrollDeduction) {
        return payrollDeductionService.save(payrollDeduction);
    }

    @PutMapping("/{id}")
    public Mono<PayrollDeduction> updateStaff(@PathVariable Long id, @RequestBody PayrollDeduction payrollDeduction) {
        payrollDeduction.setId(id);
        return payrollDeductionService.save(payrollDeduction);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<DataDto<PayrollDeduction>> getAllDeduction(@RequestParam(name = "sortBy", defaultValue = "amount") String sortBy,
                                                           @RequestParam(name = "staffId") Long staffId,
                                                           @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
                                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                                           @RequestParam(name = "size", defaultValue = "20") int size) {
        return payrollDeductionService.findAll(staffId, page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    public Mono<PayrollDeduction> getStaffById(@PathVariable Long id) {
        return payrollDeductionService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteStaffById(@PathVariable Long id) {
        return payrollDeductionService.delete(id);
    }
}

