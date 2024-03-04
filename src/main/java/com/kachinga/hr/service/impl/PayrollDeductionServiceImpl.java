package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.PayrollDeduction;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.repository.DeductionRepository;
import com.kachinga.hr.repository.PayrollDeductionRepository;
import com.kachinga.hr.repository.PayrollRepository;
import com.kachinga.hr.repository.StaffRepository;
import com.kachinga.hr.service.PayrollDeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PayrollDeductionServiceImpl implements PayrollDeductionService {
    private final StaffRepository staffRepository;
    private final DeductionRepository deductionRepository;
    private final PayrollDeductionRepository payrollDeductionRepository;
    private final PayrollRepository payrollRepository;

    @Override
    public Mono<PayrollDeduction> save(PayrollDeduction payrollDeduction) {
        return payrollDeductionRepository.findByPayrollIdAndStaffIdAndDeductionId(payrollDeduction.getPayrollId(), payrollDeduction.getStaffId(), payrollDeduction.getDeductionId())
                .flatMap(existingDeduction -> {
                    payrollDeduction.setPayrollId(payrollDeduction.getPayrollId());
                    payrollDeduction.setDeductionId(payrollDeduction.getDeductionId());
                    payrollDeduction.setStaffId(payrollDeduction.getStaffId());
                    payrollDeduction.setAmount(payrollDeduction.getAmount());
                    payrollDeduction.setId(existingDeduction.getId());
                    return payrollDeductionRepository.save(payrollDeduction);
                })
                .switchIfEmpty(Mono.defer(() -> payrollDeductionRepository.save(payrollDeduction)));
    }

    @Override
    public Mono<PayrollDeduction> getById(Long id) {
        return payrollDeductionRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return payrollDeductionRepository.deleteById(id);
    }

    private Flux<PayrollDeduction> findAllWithDetails(Long staffId, PageRequest pageRequest) {
        return payrollDeductionRepository.findByStaffId(staffId, pageRequest)
                .flatMap(payrollDeduction -> Mono.zip(
                        Mono.just(payrollDeduction),
                        staffRepository.findById(payrollDeduction.getStaffId()),
                        deductionRepository.findById(payrollDeduction.getDeductionId()),
                        payrollRepository.findById(payrollDeduction.getPayrollId())
                ).map(tuple -> {
                    payrollDeduction.setStaff(tuple.getT2());
                    payrollDeduction.setDeduction(tuple.getT3());
                    payrollDeduction.setPayroll(tuple.getT4());
                    return payrollDeduction;
                }));
    }

    @Override
    public Mono<DataDto<PayrollDeduction>> findAll(Long staffId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Flux<PayrollDeduction> deductionsFlux = findAllWithDetails(staffId, pageRequest);
        Mono<Long> countMono = payrollDeductionRepository.countAllByStaffId(staffId);
        return Mono.zip(deductionsFlux.collectList(), countMono, DataDto::new);
    }
}
