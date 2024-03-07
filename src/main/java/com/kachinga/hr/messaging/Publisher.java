package com.kachinga.hr.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Publisher {
    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> void produce(T object, String exchange, String routingKey) {
        try {
            String json = objectMapper.writeValueAsString(object);
            Message rabbitMessage = MessageBuilder
                    .withBody(json.getBytes())
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();
            amqpTemplate.send(exchange, routingKey, rabbitMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing object to JSON to send to queueing service", e);
        }
    }
}
