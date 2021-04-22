package com.zhang.commons.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;

@Service
public class ActiveMQSender { // 消息的生产者

    @Autowired
    private JmsMessagingTemplate jmsTemplate;

    public void sendChanMsg(Destination dest, String msg) {
        jmsTemplate.convertAndSend(dest, msg);
    }
}
