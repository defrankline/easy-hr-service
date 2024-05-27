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
@NoArgsConstructor
@AllArgsConstructor
@Table("payroll_expense_amounts")
public class PayrollExpenseAmount {
    @Id
    private Long id;

    @Transient
    private PayrollExpenseAccount payrollExpenseAccount;

    @Column("item_id")
    private Long itemId;

    @Transient
    private Payroll payroll;

    @Column("payroll_id")
    private Long payrollId;

    @Column("amount")
    private BigDecimal amount;
}