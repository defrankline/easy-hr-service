package com.kachinga.hr.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRepaymentRequestDto implements Serializable {
    private Long payrollId;
    private Long staffId;
    private Long clientId;
    private Long deductionId;
    private BigDecimal amount;

    public LoanRepaymentRequestDto(Long payrollId, Long staffId, Long clientId, Long deductionId) {
        this.payrollId = payrollId;
        this.staffId = staffId;
        this.clientId = clientId;
        this.deductionId = deductionId;
    }
}
