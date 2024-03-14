package com.kachinga.hr.controller;

import com.kachinga.hr.domain.DeductionAccount;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.DeductionAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/deduction-accounts")
public class DeductionAccountController {

    private final DeductionAccountService deductionAccountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DeductionAccount> create(@RequestBody DeductionAccount deductionAccount,
                                         @RequestHeader(value = "X-auth-company-id") Long companyId) {
        deductionAccount.setCompanyId(companyId);
        return deductionAccountService.save(deductionAccount);
    }

    @PutMapping("/{id}")
    public Mono<DeductionAccount> update(@PathVariable Long id, @RequestBody DeductionAccount deductionAccount,
                                         @RequestHeader(value = "X-auth-company-id") Long companyId) {
        deductionAccount.setCompanyId(companyId);
        deductionAccount.setId(id);
        return deductionAccountService.save(deductionAccount);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<DataDto<DeductionAccount>> getAllDeduction(@RequestParam(name = "sortBy", defaultValue = "amount") String sortBy,
                                                           @RequestParam(name = "deductionId") Long deductionId,
                                                           @RequestHeader(value = "X-auth-company-id") Long companyId,
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

    @GetMapping("/get-client-deduction")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BigDecimal> getClientDeduction(@RequestParam(name = "deductionId") Long deductionId,
                                               @RequestParam(name = "staffId") Long staffId,
                                               @RequestHeader(value = "X-auth-company-id") Long companyId) {
        return deductionAccountService.deductionAmount(staffId, deductionId, companyId);
    }
}

