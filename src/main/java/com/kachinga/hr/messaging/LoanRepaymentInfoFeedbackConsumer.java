package com.kachinga.hr.messaging;

import com.kachinga.hr.domain.PayrollDeduction;
import com.kachinga.hr.domain.dto.LoanRepaymentRequestDto;
import com.kachinga.hr.service.PayrollDeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanRepaymentInfoFeedbackConsumer {
    private final PayrollDeductionService payrollDeductionService;

    @RabbitListener(queues = {"${messaging.loan.repayment.info.feedback.queue}"})
    public void consume(LoanRepaymentRequestDto dto) {
        System.out.println("Received Loan Repayment Info Feedback: Client ID: " + dto.getClientId() + ", Pending Loan: " + dto.getAmount());
        payrollDeductionService.save(new PayrollDeduction(dto.getStaffId(), dto.getPayrollId(), dto.getDeductionId(), dto.getAmount()));
    }
}
