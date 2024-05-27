package com.kachinga.hr.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaymentDto {
    private Long id;
    private Long cashierId;
    private String pvNumber;
    private String paymentMethod;
    private Boolean doubleEntry;
    private List<PaymentAccountDto> items;
}
