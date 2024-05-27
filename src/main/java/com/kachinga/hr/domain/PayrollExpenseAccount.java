package com.kachinga.hr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("payroll_expense_accounts")
public class PayrollExpenseAccount {
    @Id
    private Long id;

    @Column("item")
    private String item;

    @Column("account_id")
    private Long accountId;

    @Column("projection_id")
    private Long projection_id;
}