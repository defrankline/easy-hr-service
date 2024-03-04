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

    @Column("social_security_fund")
    private BigDecimal socialSecurityFund;

    @Column("pay_as_you_earn")
    private BigDecimal payAsYouEarn;

    @Column("health_insurance")
    private BigDecimal healthInsurance;

    @Column("share")
    private BigDecimal share;

    @Column("saving")
    private BigDecimal saving;

    @Column("deposit")
    private BigDecimal deposit;

    @Column("contribution")
    private BigDecimal contribution;

    @Column("loan")
    private BigDecimal loan;
}
