package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.PayrollItem;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.repository.PayrollItemRepository;
import com.kachinga.hr.repository.PayrollRepository;
import com.kachinga.hr.repository.StaffRepository;
import com.kachinga.hr.service.PayrollItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollItemServiceImpl implements PayrollItemService {
    private final StaffRepository staffRepository;
    private final PayrollItemRepository payrollItemRepository;
    private final PayrollRepository payrollRepository;

    @Override
    public Mono<PayrollItem> save(PayrollItem payrollItem) {
        return create(payrollItem);
    }

    private Mono<PayrollItem> create(PayrollItem payrollItem) {
        return payrollItemRepository.findByPayrollIdAndStaffId(payrollItem.getPayrollId(), payrollItem.getStaffId())
                .flatMap(existingDeduction -> {
                    payrollItem.setPayrollId(payrollItem.getPayrollId());
                    payrollItem.setStaffId(payrollItem.getStaffId());
                    payrollItem.setGross(payrollItem.getGross());
                    payrollItem.setMiscellaneousEarning(payrollItem.getMiscellaneousEarning());
                    payrollItem.setId(existingDeduction.getId());
                    return payrollItemRepository.save(payrollItem);
                })
                .switchIfEmpty(Mono.defer(() -> payrollItemRepository.save(payrollItem)));
    }

    @Override
    public Flux<PayrollItem> save(List<PayrollItem> items) {
        return Flux.fromIterable(items).flatMap(this::create);
    }

    @Override
    public Mono<PayrollItem> getById(Long id) {
        return payrollItemRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return payrollItemRepository.deleteById(id);
    }

    private Flux<PayrollItem> findAllWithDetails(Long payrollId, PageRequest pageRequest) {
        return payrollItemRepository.findByPayrollId(payrollId, pageRequest)
                .flatMap(payrollItem -> Mono.zip(
                        Mono.just(payrollItem),
                        staffRepository.findById(payrollItem.getStaffId()),
                        payrollRepository.findById(payrollItem.getPayrollId())
                ).map(tuple -> {
                    payrollItem.setStaff(tuple.getT2());
                    payrollItem.setPayroll(tuple.getT3());
                    return payrollItem;
                }));
    }

    @Override
    public Mono<DataDto<PayrollItem>> findAll(Long payrollId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Flux<PayrollItem> deductionsFlux = findAllWithDetails(payrollId, pageRequest);
        Mono<Long> countMono = payrollItemRepository.countAllByPayrollId(payrollId);
        return Mono.zip(deductionsFlux.collectList(), countMono, DataDto::new);
    }
}
