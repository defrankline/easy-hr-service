package com.kachinga.hr.controller;

import com.kachinga.hr.domain.MiscEarning;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.MiscEarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/misc-earnings")
public class MiscEarningController {

    private final MiscEarningService miscEarningService;

    @GetMapping
    public Mono<ResponseEntity<DataDto<MiscEarning>>> getAllDeduction(@RequestParam(name = "sortBy", defaultValue = "amount") String sortBy,
                                                                      @RequestParam(name = "staffId") Long staffId,
                                                                      @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
                                                                      @RequestParam(name = "page", defaultValue = "0") int page,
                                                                      @RequestParam(name = "size", defaultValue = "20") int size) {
        return miscEarningService.findAll(staffId, page, size, sortBy, sortDirection)
                .map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.ok(null));
    }

    @PostMapping
    public Mono<ResponseEntity<MiscEarning>> create(@RequestBody MiscEarning miscEarning) {
        return miscEarningService.create(miscEarning)
                .map(savedMiscEarning -> new ResponseEntity<>(savedMiscEarning, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<MiscEarning>> update(@PathVariable Long id, @RequestBody MiscEarning miscEarning) {
        return miscEarningService.update(id, miscEarning)
                .map(updatedMiscEarning -> new ResponseEntity<>(updatedMiscEarning, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return miscEarningService.delete(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MiscEarning>> getById(@PathVariable Long id) {
        return miscEarningService.getById(id)
                .map(miscEarning -> new ResponseEntity<>(miscEarning, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}

