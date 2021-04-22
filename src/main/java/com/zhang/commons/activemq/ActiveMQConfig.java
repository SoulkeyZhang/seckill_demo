package com.zhang.commons.activemq;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Destination;

@Configuration
public class ActiveMQConfig {
    private Destination destination;

    public ActiveMQConfig() {
        this.destination = new ActiveMQQueue("seckill.queue"); // 记录秒杀下单的队列
    }

    @Bean
    public Destination getDestination() {
        return destination;
    }
}
