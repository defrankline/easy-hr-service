package com.kachinga.hr.controller;

import com.kachinga.hr.domain.PayrollItem;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.PayrollItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/payroll-items")
public class PayrollItemController {

    private final PayrollItemService payrollItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PayrollItem> create(@RequestBody PayrollItem payrollItem) {
        return payrollItemService.save(payrollItem);
    }

    @PutMapping("/{id}")
    public Mono<PayrollItem> create(@PathVariable Long id, @RequestBody PayrollItem payrollItem) {
        payrollItem.setId(id);
        return payrollItemService.save(payrollItem);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<DataDto<PayrollItem>> getAllDeduction(@RequestParam(name = "sortBy", defaultValue = "amount") String sortBy,
                                                      @RequestParam(name = "payrollId") Long payrollId,
                                                      @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
                                                      @RequestParam(name = "page", defaultValue = "0") int page,
                                                      @RequestParam(name = "size", defaultValue = "20") int size) {
        return payrollItemService.findAll(payrollId, page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    public Mono<PayrollItem> getById(@PathVariable Long id) {
        return payrollItemService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable Long id) {
        return payrollItemService.delete(id);
    }
}

