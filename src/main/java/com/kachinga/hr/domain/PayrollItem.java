package com.kachinga.hr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("payroll")
public class PayrollItem {
    @Id
    private Long id;

    @Transient
    private Payroll payroll;

    @Column("payroll_id")
    private Long payrollId;

    @Transient
    private Staff staff;

    @Column("staff_id")
    private Long staffId;

    @Column("gross")
    private BigDecimal gross;

    @Column("deduction")
    private BigDecimal deduction;

    @Column("miscellaneous_earning")
    private BigDecimal miscellaneousEarning;

    @Column("net")
    private BigDecimal net;

    public PayrollItem(Long payrollId, Long staffId, BigDecimal gross, BigDecimal deduction, BigDecimal miscellaneousEarning, BigDecimal net) {
        this.payrollId = payrollId;
        this.staffId = staffId;
        this.gross = gross;
        this.deduction = deduction;
        this.miscellaneousEarning = miscellaneousEarning;
        this.net = net;
    }
}
