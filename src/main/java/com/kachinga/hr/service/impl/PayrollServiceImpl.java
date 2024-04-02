package com.kachinga.hr.service.impl;

import com.kachinga.hr.domain.*;
import com.kachinga.hr.domain.dto.DataDto;
import com.kachinga.hr.domain.dto.LoanRepaymentRequestDto;
import com.kachinga.hr.domain.dto.PayrollReportDTO;
import com.kachinga.hr.messaging.Producer;
import com.kachinga.hr.repository.*;
import com.kachinga.hr.service.MiscEarningService;
import com.kachinga.hr.service.PayrollService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {
    private final PayrollRepository payrollRepository;
    private final MonthlyDeductionRepository monthlyDeductionRepository;
    private final PayrollDeductionRepository payrollDeductionRepository;
    private final PayrollItemRepository payrollItemRepository;
    private final StaffRepository staffRepository;
    private final MiscEarningService miscEarningService;
    private final Producer producer;
    private final PayrollMiscEarningRepository payrollMiscEarningRepository;
    private final R2dbcEntityTemplate template;

    @NotNull
    private static PayrollReportDTO getPayrollReportDTO(io.r2dbc.spi.Row row) {
        PayrollReportDTO item = new PayrollReportDTO();
        item.setId(row.get("id", Long.class));
        item.setStaffID(row.get("staffID", String.class));
        item.setUserId(row.get("userId", Long.class));
        item.setName(row.get("name", String.class));
        item.setBank(row.get("bank", String.class));
        item.setAccountName(row.get("accountName", String.class));
        item.setAccountNumber(row.get("accountNumber", String.class));
        item.setGross(row.get("gross", BigDecimal.class));
        item.setSocialSecurityFund(row.get("socialSecurityFund", BigDecimal.class));
        item.setHealthInsurance(row.get("healthInsurance", BigDecimal.class));
        item.setPaye(row.get("paye", BigDecimal.class));
        item.setPaye(row.get("share", BigDecimal.class));
        item.setPaye(row.get("saving", BigDecimal.class));
        item.setPaye(row.get("deposit", BigDecimal.class));
        item.setPaye(row.get("contribution", BigDecimal.class));
        item.setPaye(row.get("loanRepayment", BigDecimal.class));
        item.setPaye(row.get("miscEarning", BigDecimal.class));
        item.setPaye(row.get("net", BigDecimal.class));
        return item;
    }

    @Override
    public Mono<Payroll> create(Payroll payroll) {
        return payrollRepository.save(payroll).flatMap(savedPayroll -> run(savedPayroll.getId()));
    }

    @Override
    public Mono<Payroll> update(Long id, Payroll payroll) {
        return payrollRepository.findById(id).flatMap(existingDeduction -> {
            payroll.setName(payroll.getName());
            payroll.setDate(payroll.getDate());
            payroll.setApproved(payroll.getApproved());
            payroll.setCompanyId(payroll.getCompanyId());
            payroll.setId(existingDeduction.getId());
            return payrollRepository.save(payroll).flatMap(savedPayroll -> run(savedPayroll.getId()));
        }).switchIfEmpty(Mono.defer(() -> payrollRepository.save(payroll))).flatMap(savedPayroll -> run(savedPayroll.getId()));
    }

    @Override
    public Mono<Payroll> run(Long id) {
        return payrollRepository.findById(id).flatMap(payroll -> staffRepository.findByCompanyId(payroll.getCompanyId()).flatMap(staff -> processStaffPayroll(staff, payroll)).then(Mono.just(payroll)));
    }

    @Override
    public Mono<Payroll> getById(Long id) {
        return payrollRepository.findById(id);
    }

    public Mono<DataDto<Payroll>> findAll(Long companyId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Mono<List<Payroll>> payrollsMono = payrollRepository.findByCompanyId(companyId, pageRequest).collectList();
        Mono<Long> countMono = payrollRepository.count();
        return Mono.zip(payrollsMono, countMono, DataDto::new);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return payrollRepository.deleteById(id);
    }

    private Flux<PayrollItem> processStaffPayroll(Staff staff, Payroll payroll) {
        Flux<MiscEarning> miscEarningsFlux = miscEarningService.findByStaffIdAndRecurrentTrue(staff.getId());

        return miscEarningsFlux.collectList().flatMapMany(miscEarnings -> {
            BigDecimal totalRecurringEarningsToAddToGross = miscEarnings.stream().filter(MiscEarning::getAddedToGross).map(MiscEarning::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalRecurringEarningsToAddToNet = miscEarnings.stream().filter(miscEarning -> !miscEarning.getAddedToGross()).map(MiscEarning::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalMiscEarning = totalRecurringEarningsToAddToGross.add(totalRecurringEarningsToAddToNet);

            Mono<Void> savePayrollMiscEarningsMono = Flux.fromIterable(miscEarnings).flatMap(miscEarning -> {
                PayrollMiscEarning payrollMiscEarning = new PayrollMiscEarning(payroll.getId(), staff.getId(), miscEarning.getItemId(), miscEarning.getAmount());
                return payrollMiscEarningRepository.save(payrollMiscEarning);
            }).then();

            return savePayrollMiscEarningsMono.thenReturn(miscEarnings).flatMapMany(ignored -> monthlyDeductionRepository.findByStaffId(staff.getId())).flatMap(monthlyDeduction -> {
                PayrollDeduction payrollDeduction = new PayrollDeduction(staff.getId(), payroll.getId(), monthlyDeduction.getDeductionId(), monthlyDeduction.getAmount());
                requestLoanRepayment(payroll.getId(), staff.getId(), staff.getUserId(), monthlyDeduction.getDeductionId());
                return payrollDeductionRepository.save(payrollDeduction);
            }).collectList().flatMapMany(deductions -> {
                BigDecimal totalDeductions = deductions.stream().map(PayrollDeduction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal netSalaryBeforeMisc = staff.getSalary().add(totalRecurringEarningsToAddToGross).subtract(totalDeductions);
                BigDecimal finalNetSalary = netSalaryBeforeMisc.add(totalRecurringEarningsToAddToNet);
                PayrollItem payrollItem = new PayrollItem(payroll.getId(), staff.getId(), staff.getSalary().add(totalRecurringEarningsToAddToGross), totalDeductions, totalMiscEarning, finalNetSalary);

                return payrollItemRepository.save(payrollItem).flux();
            });
        });
    }

    private void requestLoanRepayment(Long payrollId, Long staffId, Long clientId, Long deductionId) {
        LoanRepaymentRequestDto dto = new LoanRepaymentRequestDto(payrollId, staffId, clientId, deductionId);
        producer.sendToLoanService(dto);
    }

    private Flux<PayrollReportDTO> report(Long payrollId, int page, int size) {
        String query = "SELECT s.id AS id, s.number AS \"staffID\", s.user_id as \"userId\", s.name, s.bank, s.account_name as \"accountName\", s.account_number as \"accountNumber\", pi.gross, SUM(CASE WHEN d.code = '100' THEN pd.amount ELSE 0 END) AS \"socialSecurityFund\", SUM(CASE WHEN d.code = '101' THEN pd.amount ELSE 0 END) AS \"healthInsurance\", SUM(CASE WHEN d.code = '102' THEN pd.amount ELSE 0 END) AS \"paye\", SUM(CASE WHEN d.code = '108' THEN pd.amount ELSE 0 END) AS \"studentLoan\", SUM(CASE WHEN d.code = '103' THEN pd.amount ELSE 0 END) AS \"share\", SUM(CASE WHEN d.code = '104' THEN pd.amount ELSE 0 END) AS \"saving\", SUM(CASE WHEN d.code = '105' THEN pd.amount ELSE 0 END) AS \"deposit\", SUM(CASE WHEN d.code = '106' THEN pd.amount ELSE 0 END) AS \"contribution\", SUM(CASE WHEN d.code = '107' THEN pd.amount ELSE 0 END) AS \"loanRepayment\", pi.miscellaneous_earning as miscEarning, pi.net FROM staff s JOIN payroll_items pi ON s.id = pi.staff_id LEFT JOIN payroll_deductions pd ON s.id = pd.staff_id AND pi.payroll_id = pd.payroll_id INNER JOIN deductions d on d.id = pd.deduction_id WHERE pi.payroll_id = :payrollId GROUP BY s.id, pi.gross, pi.miscellaneous_earning, pi.net ORDER BY s.id LIMIT :size offset :offset";
        int offset = page * size;
        return this.template.getDatabaseClient().sql(query).bind("payrollId", payrollId).bind("size", size).bind("offset", offset).map((row, metadata) -> getPayrollReportDTO(row)).all();
    }

    private Flux<PayrollReportDTO> report(Long payrollId) {
        String query = "SELECT s.id AS id, s.number AS \"staffID\", s.user_id as \"userId\", s.name, s.bank, s.account_name as \"accountName\", s.account_number as \"accountNumber\", pi.gross, SUM(CASE WHEN d.code = '100' THEN pd.amount ELSE 0 END) AS \"socialSecurityFund\", SUM(CASE WHEN d.code = '101' THEN pd.amount ELSE 0 END) AS \"healthInsurance\", SUM(CASE WHEN d.code = '102' THEN pd.amount ELSE 0 END) AS \"paye\", SUM(CASE WHEN d.code = '108' THEN pd.amount ELSE 0 END) AS \"studentLoan\", SUM(CASE WHEN d.code = '103' THEN pd.amount ELSE 0 END) AS \"share\", SUM(CASE WHEN d.code = '104' THEN pd.amount ELSE 0 END) AS \"saving\", SUM(CASE WHEN d.code = '105' THEN pd.amount ELSE 0 END) AS \"deposit\", SUM(CASE WHEN d.code = '106' THEN pd.amount ELSE 0 END) AS \"contribution\", SUM(CASE WHEN d.code = '107' THEN pd.amount ELSE 0 END) AS \"loanRepayment\", pi.miscellaneous_earning as miscEarning, pi.net FROM staff s JOIN payroll_items pi ON s.id = pi.staff_id LEFT JOIN payroll_deductions pd ON s.id = pd.staff_id AND pi.payroll_id = pd.payroll_id INNER JOIN deductions d on d.id = pd.deduction_id WHERE pi.payroll_id = :payrollId GROUP BY s.id, pi.gross, pi.miscellaneous_earning, pi.net ORDER BY s.id";
        return this.template.getDatabaseClient().sql(query).bind("payrollId", payrollId).map((row, metadata) -> getPayrollReportDTO(row)).all();
    }

    private Mono<Long> countPayrollItems(Long payrollId) {
        String countQuery = "select count(*) from (SELECT s.id AS id, s.number AS \"staffID\", s.user_id as \"userId\", s.name, s.bank, s.account_name as \"accountName\", s.account_number as \"accountNumber\", pi.gross, SUM(CASE WHEN d.code = '100' THEN pd.amount ELSE 0 END) AS \"socialSecurityFund\", SUM(CASE WHEN d.code = '101' THEN pd.amount ELSE 0 END) AS \"healthInsurance\", SUM(CASE WHEN d.code = '102' THEN pd.amount ELSE 0 END) AS \"paye\", SUM(CASE WHEN d.code = '108' THEN pd.amount ELSE 0 END) AS \"studentLoan\", SUM(CASE WHEN d.code = '103' THEN pd.amount ELSE 0 END) AS \"share\", SUM(CASE WHEN d.code = '104' THEN pd.amount ELSE 0 END) AS \"saving\", SUM(CASE WHEN d.code = '105' THEN pd.amount ELSE 0 END) AS \"deposit\", SUM(CASE WHEN d.code = '106' THEN pd.amount ELSE 0 END) AS \"contribution\", SUM(CASE WHEN d.code = '107' THEN pd.amount ELSE 0 END) AS \"loanRepayment\", pi.miscellaneous_earning as miscEarning, pi.net FROM staff s JOIN payroll_items pi ON s.id = pi.staff_id LEFT JOIN payroll_deductions pd ON s.id = pd.staff_id AND pi.payroll_id = pd.payroll_id INNER JOIN deductions d on d.id = pd.deduction_id WHERE pi.payroll_id = :payrollId GROUP BY s.id, pi.gross, pi.miscellaneous_earning, pi.net ORDER BY s.id) v";
        return this.template.getDatabaseClient().sql(countQuery).bind("payrollId", payrollId).map((row, meta) -> row.get(0, Long.class)).one();
    }

    @Override
    public Mono<DataDto<PayrollReportDTO>> payroll(Long payrollId, int page, int size) {
        Flux<PayrollReportDTO> payrollItemsFlux = report(payrollId, page, size);
        Mono<Long> countMono = countPayrollItems(payrollId);
        return payrollItemsFlux.collectList().zipWith(countMono).map(tuple -> new DataDto<>(tuple.getT1(), tuple.getT2()));
    }

    @Override
    public Flux<PayrollReportDTO> payroll(Long payrollId) {
        return report(payrollId);
    }

    @Override
    public Mono<byte[]> download(Long payrollId) {
        return this.payroll(payrollId).collectList().handle((items, sink) -> {
            try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                Sheet sheet = workbook.createSheet("Payroll");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("ID");
                headerRow.createCell(1).setCellValue("Name");
                headerRow.createCell(2).setCellValue("Bank");
                headerRow.createCell(3).setCellValue("Account Name");
                headerRow.createCell(4).setCellValue("Account Number");
                headerRow.createCell(5).setCellValue("Basic Salary");
                headerRow.createCell(6).setCellValue("Social Security Fund");
                headerRow.createCell(7).setCellValue("Health Insurance");
                headerRow.createCell(8).setCellValue("Paye");
                headerRow.createCell(9).setCellValue("Student Loan");
                headerRow.createCell(10).setCellValue("Share");
                headerRow.createCell(11).setCellValue("Saving");
                headerRow.createCell(12).setCellValue("Deposit");
                headerRow.createCell(13).setCellValue("Jamii");
                headerRow.createCell(14).setCellValue("Loan Repayment");
                headerRow.createCell(15).setCellValue("Misc. Earning");
                headerRow.createCell(16).setCellValue("Net(Take Home)");

                for (int i = 0; i < items.size(); i++) {
                    PayrollReportDTO item = items.get(i);
                    Row row = sheet.createRow(i + 1);
                    row.createCell(0).setCellValue(item.getStaffID());
                    row.createCell(1).setCellValue(item.getName());
                    row.createCell(2).setCellValue(item.getBank());
                    row.createCell(3).setCellValue(item.getAccountName());
                    row.createCell(4).setCellValue(item.getAccountNumber());
                    row.createCell(5).setCellValue(item.getGross() != null ? item.getGross().doubleValue() : 0);
                    row.createCell(6).setCellValue(item.getSocialSecurityFund() != null ? item.getSocialSecurityFund().doubleValue() : 0);
                    row.createCell(7).setCellValue(item.getHealthInsurance() != null ? item.getHealthInsurance().doubleValue() : 0);
                    row.createCell(8).setCellValue(item.getPaye() != null ? item.getPaye().doubleValue() : 0);
                    row.createCell(9).setCellValue(item.getStudentLoan() != null ? item.getStudentLoan().doubleValue() : 0);
                    row.createCell(10).setCellValue(item.getShare() != null ? item.getShare().doubleValue() : 0);
                    row.createCell(11).setCellValue(item.getSaving() != null ? item.getSaving().doubleValue() : 0);
                    row.createCell(12).setCellValue(item.getDeposit() != null ? item.getDeposit().doubleValue() : 0);
                    row.createCell(13).setCellValue(item.getContribution() != null ? item.getContribution().doubleValue() : 0);
                    row.createCell(14).setCellValue(item.getLoanRepayment() != null ? item.getLoanRepayment().doubleValue() : 0);
                    row.createCell(15).setCellValue(item.getMiscEarning() != null ? item.getMiscEarning().doubleValue() : 0);
                    row.createCell(16).setCellValue(item.getNet() != null ? item.getNet().doubleValue() : 0);
                }
                workbook.write(bos);
                sink.next(bos.toByteArray());
            } catch (IOException e) {
                sink.error(new RuntimeException("Failed to generate Excel file", e));
            }
        });
    }
}
