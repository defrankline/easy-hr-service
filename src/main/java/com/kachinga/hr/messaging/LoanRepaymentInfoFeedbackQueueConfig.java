package com.kachinga.hr.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoanRepaymentInfoFeedbackQueueConfig {

    @Value("${messaging.loan.repayment.info.feedback.queue}")
    private String feedbackQueue;

    @Value("${messaging.loan.repayment.info.feedback.exchange}")
    private String feedbackExchange;

    @Value("${messaging.loan.repayment.info.feedback.routingKey}")
    private String feedbackRoutingKey;

    @Bean
    public Queue feedbackQueue() {
        return new Queue(feedbackQueue, true);
    }

    @Bean
    public TopicExchange feedbackExchange() {
        return new TopicExchange(feedbackExchange);
    }

    @Bean
    public Binding feedbackBinding() {
        return BindingBuilder.bind(feedbackQueue()).to(feedbackExchange()).with(feedbackRoutingKey);
    }
}
