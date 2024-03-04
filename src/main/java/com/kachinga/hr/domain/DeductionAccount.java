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
@Table("deduction_accounts")
public class DeductionAccount {
    @Id
    private Long id;

    @Column("account_id")
    private Long accountId;

    @Transient
    private Deduction deduction;

    @Column("deduction_id")
    private Long deductionId;

    @Column("company_id")
    private Long companyId;

    @Column("fixed_amount")
    private BigDecimal fixedAmount;

    @Column("percentage")
    private Double percentage;
}
