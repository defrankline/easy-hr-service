package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.MiscEarningItem;
import com.kachinga.hr.domain.Staff;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.repository.MiscEarningItemRepository;
import com.kachinga.hr.repository.StaffRepository;
import com.kachinga.hr.service.MiscEarningItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MiscEarningItemServiceImpl implements MiscEarningItemService {
    private final MiscEarningItemRepository miscEarningItemRepository;
    private final StaffRepository staffRepository;

    @Override
    public Mono<MiscEarningItem> create(MiscEarningItem miscEarningItem) {
        miscEarningItem.setId(null);
        return miscEarningItemRepository.save(miscEarningItem);
    }

    @Override
    public Mono<MiscEarningItem> update(Long id, MiscEarningItem miscEarningItem) {
        return miscEarningItemRepository.findById(id).flatMap(existingDeduction -> {
            existingDeduction.setName(miscEarningItem.getName());
            existingDeduction.setAccountId(miscEarningItem.getAccountId());
            existingDeduction.setFixedAmount(miscEarningItem.getFixedAmount());
            existingDeduction.setPercentage(miscEarningItem.getPercentage());
            return miscEarningItemRepository.save(existingDeduction);
        }).switchIfEmpty(Mono.error(new RuntimeException("Misc Earning not found with id " + miscEarningItem.getId())));
    }

    @Override
    public Mono<MiscEarningItem> getById(Long id) {
        return miscEarningItemRepository.findById(id);
    }

    @Override
    public Mono<MiscEarningItem> findByNameAndCompanyId(String name, Long companyId) {
        return miscEarningItemRepository.findByNameAndCompanyId(name, companyId);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return miscEarningItemRepository.deleteById(id);
    }

    @Override
    public Mono<DataDto<MiscEarningItem>> findAll(Long companyId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Flux<MiscEarningItem> deductionsFlux = miscEarningItemRepository.findByCompanyId(companyId, pageRequest);
        Mono<Long> countMono = miscEarningItemRepository.countAllByCompanyId(companyId);
        return Mono.zip(deductionsFlux.collectList(), countMono, DataDto::new);
    }

    @Override
    public Mono<BigDecimal> earningAmount(Long staffId, Long itemId) {
        Mono<Staff> staffMono = staffRepository.findById(staffId);
        Mono<MiscEarningItem> itemMono = this.getById(itemId);

        return Mono.zip(staffMono, itemMono).flatMap(tuple -> {
            Staff staff = tuple.getT1();
            MiscEarningItem miscEarningItem = tuple.getT2();

            BigDecimal grossSalary = staff.getSalary();
            BigDecimal fixedAmount = miscEarningItem.getFixedAmount() != null ? miscEarningItem.getFixedAmount() : BigDecimal.ZERO;
            double percentage = miscEarningItem.getPercentage() != null ? miscEarningItem.getPercentage() : 0.0;
            BigDecimal earning = grossSalary.multiply(BigDecimal.valueOf(0.01)).multiply(new BigDecimal(percentage)).add(fixedAmount);
            return Mono.just(earning);
        });
    }
}
