package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.Deduction;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.repository.DeductionRepository;
import com.kachinga.hr.service.DeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionServiceImpl implements DeductionService {
    private final DeductionRepository deductionRepository;

    @Override
    public Mono<Deduction> save(Deduction deduction) {
        return deductionRepository.save(deduction);
    }

    @Override
    public Mono<Deduction> getById(Long id) {
        return deductionRepository.findById(id);
    }

    @Override
    public Mono<Deduction> getByCode(String code) {
        return deductionRepository.findByCode(code);
    }

    public Mono<DataDto<Deduction>> findAll(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Mono<List<Deduction>> deductionsMono = deductionRepository.findAllBy(pageRequest).collectList();
        Mono<Long> countMono = deductionRepository.count();
        return Mono.zip(deductionsMono, countMono, DataDto::new);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return deductionRepository.deleteById(id);
    }

    /*public BigDecimal payAsYouEarn(BigDecimal basicSalary) {
        double percent = employeePercent;
        BigDecimal percentage = new BigDecimal(percent);
        BigDecimal ssf = (percentage.multiply(basicSalary)).divide(new BigDecimal(100), 9, RoundingMode.CEILING);
        EmployeeEmployerContributionDto hif = hif(companyId, basicSalary);
        BigDecimal taxableAmount = basicSalary.subtract(ssf.add(hif.getEmployee()));
        if (taxableAmount.compareTo(new BigDecimal(270000)) <= 0) {
            return BigDecimal.ZERO;
        } else if (taxableAmount.compareTo(new BigDecimal(270000)) > 0 && taxableAmount.compareTo(new BigDecimal(520000)) <= 0) {
            BigDecimal balance = taxableAmount.subtract(new BigDecimal(270000));
            BigDecimal employeeContribution = ((balance.multiply(new BigDecimal(9))).divide(new BigDecimal(100), 9, RoundingMode.CEILING));
            return new EmployeeEmployerContributionDto(employeeContribution, employerContribution);
        } else if (taxableAmount.compareTo(new BigDecimal(520000)) > 0 && taxableAmount.compareTo(new BigDecimal(760000)) <= 0) {
            BigDecimal balance = taxableAmount.subtract(new BigDecimal(520000));
            BigDecimal employeeContribution = ((balance.multiply(new BigDecimal(20))).divide(new BigDecimal(100), 9, RoundingMode.CEILING)).add(new BigDecimal(22500));
            BigDecimal employerContribution = (new BigDecimal(employerPercent / employeePercent)).multiply(employeeContribution);
            return new EmployeeEmployerContributionDto(employeeContribution, employerContribution);
        } else if (taxableAmount.compareTo(new BigDecimal(760000)) > 0 && taxableAmount.compareTo(new BigDecimal(1000000)) <= 0) {
            BigDecimal balance = taxableAmount.subtract(new BigDecimal(760000));
            BigDecimal employeeContribution = ((balance.multiply(new BigDecimal(25))).divide(new BigDecimal(100), 9, RoundingMode.CEILING)).add(new BigDecimal(70500));
            BigDecimal employerContribution = (new BigDecimal(employerPercent / employeePercent)).multiply(employeeContribution);
            return new EmployeeEmployerContributionDto(employeeContribution, employerContribution);
        } else {
            BigDecimal balance = taxableAmount.subtract(new BigDecimal(1000000));
            BigDecimal a = new BigDecimal(30);
            BigDecimal c = new BigDecimal(100);
            BigDecimal d = new BigDecimal(130500);
            BigDecimal e = (a.multiply(balance)).divide(c, 9, RoundingMode.CEILING);
            BigDecimal employeeContribution = e.add(d);
            BigDecimal employerContribution = (new BigDecimal(employerPercent / employeePercent)).multiply(employeeContribution);
            return new EmployeeEmployerContributionDto(employeeContribution, employerContribution);
        }
    }*/
}
