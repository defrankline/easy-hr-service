package com.kachinga.hr.messaging;

import com.kachinga.hr.domain.dto.LoanRepaymentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Producer {

    private final Publisher publisher;

    @Value("${messaging.loan.repayment.info.request.exchange}")
    private String exchange;

    @Value("${messaging.loan.repayment.info.request.routingKey}")
    private String routingKey;

    public void sendToLoanService(LoanRepaymentRequestDto dto) {
        publisher.produce(dto, exchange, routingKey);
    }
}