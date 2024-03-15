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
@Table("misc_earning_items")
public class MiscEarningItem {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("amount")
    private BigDecimal amount;

    @Column("recurrent")
    private Boolean recurrent;

    @Column("added_to_gross")
    private Boolean addedToGross;
}