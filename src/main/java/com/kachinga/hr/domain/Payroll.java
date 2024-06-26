package com.kachinga.hr.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("payroll")
public class Payroll {
    @Id
    private Long id;

    @NotNull(message = "Name is required")
    @Column("name")
    private String name;

    @NotNull(message = "Approval Status is required")
    @Column("approved")
    private Boolean approved;

    @NotNull(message = "Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column("date")
    private LocalDate date;

    @Column("company_id")
    private Long companyId;
}
