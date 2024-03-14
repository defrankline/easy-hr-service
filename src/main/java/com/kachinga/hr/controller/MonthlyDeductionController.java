package com.kachinga.hr.controller;

import com.kachinga.hr.domain.MonthlyDeduction;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.MonthlyDeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/monthly-deductions")
public class MonthlyDeductionController {

    private final MonthlyDeductionService monthlyDeductionService;

    @PostMapping
    public Mono<ResponseEntity<MonthlyDeduction>> createStaff(@RequestBody MonthlyDeduction monthlyDeduction,
                                                              ServerWebExchange exchange) {
        return monthlyDeductionService.save(monthlyDeduction).map(createdMonthlyDeduction -> {
            String path = exchange.getRequest().getPath().value();
            URI location = UriComponentsBuilder.fromPath(path).path("/{id}").buildAndExpand(createdMonthlyDeduction.getId()).toUri();
            return ResponseEntity.created(location).body(createdMonthlyDeduction);
        }).onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<MonthlyDeduction>> updateStaff(@PathVariable Long id, @RequestBody MonthlyDeduction monthlyDeduction) {
        monthlyDeduction.setId(id);
        return monthlyDeductionService.save(monthlyDeduction).map(updatedMonthlyDeduction -> ResponseEntity.ok().body(updatedMonthlyDeduction)).defaultIfEmpty(ResponseEntity.notFound().build());
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
    public Mono<ResponseEntity<MonthlyDeduction>> getId(@PathVariable Long id) {
        return monthlyDeductionService.getById(id).map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return monthlyDeductionService.delete(id).then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }
}

