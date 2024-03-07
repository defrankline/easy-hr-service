package com.kachinga.hr.controller;

import com.kachinga.hr.domain.DeductionAccount;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.DeductionAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/deduction-accounts")
public class DeductionAccountController {

    private final DeductionAccountService deductionAccountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DeductionAccount> create(@RequestBody DeductionAccount deductionAccount) {
        return deductionAccountService.save(deductionAccount);
    }

    @PutMapping("/{id}")
    public Mono<DeductionAccount> update(@PathVariable Long id, @RequestBody DeductionAccount deductionAccount) {
        deductionAccount.setId(id);
        return deductionAccountService.save(deductionAccount);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<DataDto<DeductionAccount>> getAllDeduction(@RequestParam(name = "sortBy", defaultValue = "amount") String sortBy,
                                                           @RequestParam(name = "deductionId") Long deductionId,
                                                           @RequestParam(name = "companyId") Long companyId,
                                                           @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
                                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                                           @RequestParam(name = "size", defaultValue = "20") int size) {
        return deductionAccountService.findAll(deductionId, companyId, page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    public Mono<DeductionAccount> getStaffById(@PathVariable Long id) {
        return deductionAccountService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteStaffById(@PathVariable Long id) {
        return deductionAccountService.delete(id);
    }
}

