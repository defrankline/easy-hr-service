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
@Table("payroll_deductions")
public class PayrollDeduction {
    @Id
    private Long id;

    @Transient
    private Staff staff;

    @Column("staff_id")
    private Long staffId;

    @Transient
    private Payroll payroll;

    @Column("payroll_id")
    private Long payrollId;

    @Transient
    private Deduction deduction;

    @Column("deduction_id")
    private Long deductionId;

    @Column("amount")
    private BigDecimal amount;
}
