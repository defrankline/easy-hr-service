package com.kachinga.hr.controller;

import com.kachinga.hr.domain.Staff;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.service.StaffService;
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
@RequestMapping(API + "/staff")
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    public Mono<ResponseEntity<Staff>> create(@RequestBody Staff staff, ServerWebExchange exchange,
                                              @RequestHeader(value = "X-auth-company-id") Long companyId) {
        staff.setCompanyId(companyId);
        return staffService.create(staff).map(createdStaff -> {
            String path = exchange.getRequest().getPath().value();
            URI location = UriComponentsBuilder.fromPath(path).path("/{id}").buildAndExpand(createdStaff.getId()).toUri();
            return ResponseEntity.created(location).body(createdStaff);
        }).onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Staff>> updateStaff(@PathVariable Long id, @RequestBody Staff staff,
                                                   @RequestHeader(value = "X-auth-company-id") Long companyId) {
        staff.setCompanyId(companyId);
        return staffService.update(id, staff).map(updatedStaff -> ResponseEntity.ok().body(updatedStaff)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<DataDto<Staff>>> getAllDeduction(@RequestParam(name = "sortBy", defaultValue = "number") String sortBy,
                                                                @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
                                                                @RequestParam(name = "page", defaultValue = "0") int page,
                                                                @RequestHeader(value = "X-auth-company-id") Long companyId,
                                                                @RequestParam(name = "size", defaultValue = "20") int size) {
        return staffService.findAll(companyId, page, size, sortBy, sortDirection)
                .map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.ok(null));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Staff>> getStaffById(@PathVariable Long id) {
        return staffService.getById(id).map(r -> ResponseEntity.ok().body(r)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteStaffById(@PathVariable Long id) {
        return staffService.delete(id).then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }
}

