package com.kachinga.hr.domain.dto;

import com.kachinga.hr.domain.Deduction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeductionAccountDto implements Serializable {
    private Long id;
    private Long accountId;
    private Long deductionId;
    private Double percentage;
    private BigDecimal fixedAmount;
    private Deduction deduction;
}

