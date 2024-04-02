package com.kachinga.hr.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayrollReportDTO implements Serializable {
    private Long id;
    private String staffID;
    private Long userId;
    private String name;
    private String bank;
    private String accountName;
    private String accountNumber;
    private BigDecimal gross;
    private BigDecimal socialSecurityFund;
    private BigDecimal healthInsurance;
    private BigDecimal paye;
    private BigDecimal studentLoan;
    private BigDecimal share;
    private BigDecimal saving;
    private BigDecimal deposit;
    private BigDecimal contribution;
    private BigDecimal loanRepayment;
    private BigDecimal miscEarning;
    private BigDecimal net;
}
