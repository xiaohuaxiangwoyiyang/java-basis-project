package com.example.payproject.service.impl;

import com.example.payproject.PayProjectApplicationTests;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class PayServiceImplTest extends PayProjectApplicationTests {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void sendMQMsg() {
        amqpTemplate.convertAndSend("payNotify", "hello");
    }
}
