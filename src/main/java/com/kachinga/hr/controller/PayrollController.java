package com.kachinga.hr.controller;

import com.kachinga.hr.domain.Payroll;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.domain.dto.PayrollReportDTO;
import com.kachinga.hr.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping
    public Mono<ResponseEntity<Payroll>> create(@RequestBody Payroll payroll, @RequestHeader(value = "X-auth-company-id") Long companyId) {
        payroll.setCompanyId(companyId);
        payroll.setId(null);
        return payrollService.create(payroll).map(savedMiscEarning -> new ResponseEntity<>(savedMiscEarning, HttpStatus.CREATED)).defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Payroll>> update(@PathVariable Long id, @RequestBody Payroll payroll, @RequestHeader(value = "X-auth-company-id") Long companyId) {
        payroll.setCompanyId(companyId);
        payroll.setId(id);
        return payrollService.update(id, payroll).map(updated -> ResponseEntity.ok().body(updated)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<DataDto<Payroll>>> getAll(@RequestHeader(value = "X-auth-company-id") Long companyId, @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "20") int size, @RequestParam(name = "sortBy", defaultValue = "date") String sortBy, @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection) {

        return payrollService.findAll(companyId, page, size, sortBy, sortDirection).map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.ok(null));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Payroll>> getId(@PathVariable Long id) {
        return payrollService.getById(id).map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return payrollService.delete(id).then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping("/view/{id}")
    public Mono<ResponseEntity<DataDto<PayrollReportDTO>>> view(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return payrollService.payroll(id, page, size)
                .map(dataDto -> ResponseEntity.ok().body(dataDto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/download/{id}", produces = "application/vnd.ms-excel")
    public Mono<ResponseEntity<byte[]>> download(@PathVariable("id") Long id) {
        return payrollService.download(id)
                .map(data -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"payroll-items.xlsx\"")
                        .body(data));
    }
}

