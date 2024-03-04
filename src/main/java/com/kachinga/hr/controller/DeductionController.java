package com.kachinga.hr.controller;

import com.kachinga.hr.domain.Deduction;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.DeductionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static com.kachinga.hr.util.Config.API;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + "/deductions")
public class DeductionController {

    private final DeductionService deductionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Deduction> create(@Valid @RequestBody Deduction deduction) {
        return deductionService.save(deduction)
                .onErrorResume(e -> e instanceof DataIntegrityViolationException,
                        e -> Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate Data")));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<DataDto<Deduction>> getAllDeduction(@RequestParam(name = "sortBy", defaultValue = "code") String sortBy,
                                                    @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
                                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                                    @RequestParam(name = "size", defaultValue = "20") int size) {
        return deductionService.findAll(page, size, sortBy, sortDirection);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Deduction> update(@PathVariable("id") Long id, @Valid @RequestBody Deduction deductionDTO) {
        return deductionService.getById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Deduction not found")))
                .flatMap(row -> deductionService.getByCode(deductionDTO.getCode())
                        .filter(deduction -> !deduction.getId().equals(id))
                        .flatMap(deduction -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code must be unique")))
                        .then(Mono.just(row)))
                .flatMap(deduction -> {
                    deduction.setName(deductionDTO.getName());
                    deduction.setCode(deductionDTO.getCode());
                    return deductionService.save(deduction);
                });
    }

    @GetMapping("/{id}")
    public Mono<Deduction> getDeductionById(@PathVariable Long id) {
        return deductionService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteDeductionById(@PathVariable Long id) {
        return deductionService.delete(id);
    }
}

