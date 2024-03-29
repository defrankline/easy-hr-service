package com.kachinga.hr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("payroll_misc_earnings")
public class PayrollMiscEarning {
    @Id
    private Long id;

    @Column("payroll_id")
    private Long payrollId;

    @Column("staff_id")
    private Long staffId;

    @Column("item_id")
    private Long itemId;

    @Column("amount")
    private BigDecimal amount;

    public PayrollMiscEarning(Long payrollId, Long staffId, Long itemId, BigDecimal amount) {
        this.payrollId = payrollId;
        this.staffId = staffId;
        this.itemId = itemId;
        this.amount = amount;
    }
}