package com.kachinga.hr.service;

import com.kachinga.hr.domain.Payroll;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.domain.dto.PayrollReportDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface PayrollService {
    Mono<Payroll> create(Payroll payroll);

    Mono<Payroll> update(Long id, Payroll payroll);

    Mono<Payroll> getById(Long id);

    Mono<DataDto<Payroll>> findAll(Long companyId, int page, int size, String sortBy, String sortDirection);

    Mono<Void> delete(Long id);

    Mono<Payroll> run(Long id);

    Mono<DataDto<PayrollReportDTO>> payroll(Long payrollId, int page, int size);

    Flux<PayrollReportDTO> payroll(Long payrollId);

    Mono<byte[]> download(Long payrollId);
}
