package com.kachinga.hr.controller;

import com.kachinga.hr.domain.Payroll;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping
    public Mono<ResponseEntity<Payroll>> create(@RequestBody Payroll payroll, ServerWebExchange exchange,
                                                @RequestHeader(value = "X-auth-company-id") Long companyId) {
        payroll.setCompanyId(companyId);
        payroll.setId(null);
        return payrollService.save(payroll).map(created -> {
            String path = exchange.getRequest().getPath().value();
            URI location = UriComponentsBuilder.fromPath(path).path("/{id}").buildAndExpand(created.getId()).toUri();
            return ResponseEntity.created(location).body(created);
        }).onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Payroll>> update(@PathVariable Long id, @RequestBody Payroll payroll,
                                                @RequestHeader(value = "X-auth-company-id") Long companyId) {
        payroll.setCompanyId(companyId);
        payroll.setId(id);
        return payrollService.save(payroll)
                .map(updated -> ResponseEntity.ok().body(updated)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<DataDto<Payroll>>> getAll(@RequestHeader(value = "X-auth-company-id") Long companyId,
                                                         @RequestParam(name = "page", defaultValue = "0") int page,
                                                         @RequestParam(name = "size", defaultValue = "20") int size,
                                                         @RequestParam(name = "sortBy", defaultValue = "date") String sortBy,
                                                         @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection) {

        return payrollService.findAll(companyId, page, size, sortBy, sortDirection)
                .map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.ok(null));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Payroll>> getId(@PathVariable Long id) {
        return payrollService.getById(id).map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return payrollService.delete(id).then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }
}

