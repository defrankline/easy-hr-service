package com.kachinga.hr.controller;

import com.kachinga.hr.domain.DeductionAccount;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.domain.dto.DeductionAccountDto;
import com.kachinga.hr.service.DeductionAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/deduction-accounts")
public class DeductionAccountController {

    private final DeductionAccountService deductionAccountService;

    @PostMapping
    public Mono<ResponseEntity<DeductionAccount>> create(@RequestBody DeductionAccount deductionAccount, ServerWebExchange exchange,
                                                         @RequestHeader(value = "X-auth-company-id") Long companyId) {
        deductionAccount.setCompanyId(companyId);
        return deductionAccountService.create(deductionAccount).map(createdStaff -> {
            String path = exchange.getRequest().getPath().value();
            URI location = UriComponentsBuilder.fromPath(path).path("/{id}").buildAndExpand(createdStaff.getId()).toUri();
            return ResponseEntity.created(location).body(createdStaff);
        }).onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<DeductionAccount>> updateStaff(@PathVariable Long id, @RequestBody DeductionAccount deductionAccount,
                                                              @RequestHeader(value = "X-auth-company-id") Long companyId) {
        deductionAccount.setCompanyId(companyId);
        return deductionAccountService.update(id, deductionAccount).map(updatedStaff -> ResponseEntity.ok().body(updatedStaff)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<DataDto<DeductionAccountDto>>> getAllDeduction(@RequestParam(name = "sortBy", defaultValue = "fixedAmount") String sortBy,
                                                                              @RequestHeader(value = "X-auth-company-id") Long companyId,
                                                                              @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
                                                                              @RequestParam(name = "page", defaultValue = "0") int page,
                                                                              @RequestParam(name = "size", defaultValue = "20") int size) {
        return deductionAccountService.findAll(companyId, page, size, sortBy, sortDirection)
                .map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.ok(null));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DeductionAccountDto>> getId(@PathVariable Long id) {
        return deductionAccountService.getById(id).map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return deductionAccountService.delete(id).then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping("/get-client-deduction")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BigDecimal> getClientDeduction(@RequestParam(name = "deductionId") Long deductionId,
                                               @RequestParam(name = "staffId") Long staffId,
                                               @RequestHeader(value = "X-auth-company-id") Long companyId) {
        return deductionAccountService.deductionAmount(staffId, deductionId, companyId);
    }
}

