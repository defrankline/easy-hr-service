package com.kachinga.hr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("staff")
public class Staff {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("bank")
    private String bank;

    @Column("account_name")
    private String accountName;

    @Column("account_number")
    private String accountNumber;

    @Column("user_id")
    private Long userId;

    @Column("company_id")
    private Long companyId;

    @Column("salary")
    private BigDecimal salary;

    @Column("number")
    private String number;

    @Column("title")
    private String title;
}
