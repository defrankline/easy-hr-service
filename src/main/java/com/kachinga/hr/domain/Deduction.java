package com.kachinga.hr.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("deductions")
public class Deduction {
    @Id
    private Long id;

    @NotNull(message = "Code is required")
    @Column("code")
    private String code;

    @NotNull(message = "Name is required")
    @Column("name")
    private String name;
}
