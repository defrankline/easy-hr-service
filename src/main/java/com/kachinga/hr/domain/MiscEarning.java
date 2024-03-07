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
@Table("misc_earnings")
public class MiscEarning {
    @Id
    private Long id;

    @Column("staff_id")
    private Long staffId;

    @Column("item")
    private String item;

    @Column("amount")
    private BigDecimal amount;

    @Column("recurrent")
    private Boolean recurrent;

    @Column("added_to_gross")
    private Boolean addedToGross;
}