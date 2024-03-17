package com.kachinga.hr.controller;

import com.kachinga.hr.domain.MiscEarningItem;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.MiscEarningItemService;
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
@RequestMapping(API + "/misc-earning-items")
public class MiscEarningItemController {

    private final MiscEarningItemService miscEarningItemService;

    @PostMapping
    public Mono<ResponseEntity<MiscEarningItem>> create(@RequestBody MiscEarningItem miscEarningItem, ServerWebExchange exchange,
                                                        @RequestHeader(value = "X-auth-company-id") Long companyId) {
        miscEarningItem.setCompanyId(companyId);
        return miscEarningItemService.create(miscEarningItem).map(created -> {
            String path = exchange.getRequest().getPath().value();
            URI location = UriComponentsBuilder.fromPath(path).path("/{id}").buildAndExpand(created.getId()).toUri();
            return ResponseEntity.created(location).body(created);
        }).onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<MiscEarningItem>> update(@PathVariable Long id, @RequestBody MiscEarningItem miscEarningItem,
                                                        @RequestHeader(value = "X-auth-company-id") Long companyId) {
        miscEarningItem.setCompanyId(companyId);
        return miscEarningItemService.update(id, miscEarningItem)
                .map(updated -> ResponseEntity.ok().body(updated)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<DataDto<MiscEarningItem>>> getAll(@RequestHeader(value = "X-auth-company-id") Long companyId,
                                                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                                                 @RequestParam(name = "size", defaultValue = "20") int size,
                                                                 @RequestParam(name = "sortBy", defaultValue = "fixedAmount") String sortBy,
                                                                 @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {

        return miscEarningItemService.findAll(companyId, page, size, sortBy, sortDirection)
                .map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.ok(null));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MiscEarningItem>> getId(@PathVariable Long id) {
        return miscEarningItemService.getById(id).map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return miscEarningItemService.delete(id).then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping("/get-staff-earning")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BigDecimal> getStaffDeduction(@RequestParam(name = "itemId") Long itemId,
                                              @RequestParam(name = "staffId") Long staffId) {
        return miscEarningItemService.earningAmount(staffId, itemId);
    }
}

