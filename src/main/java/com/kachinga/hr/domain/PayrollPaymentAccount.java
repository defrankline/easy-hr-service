package com.kachinga.hr.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("payroll_payment_accounts")
public class PayrollPaymentAccount {

    @Id
    private Long id;

    @Transient
    private Payroll payroll;

    @Column("payroll_id")
    private Long payrollId;

    @Column("account_id")
    private Long accountId;

    @Column("amount")
    private BigDecimal amount;
}
