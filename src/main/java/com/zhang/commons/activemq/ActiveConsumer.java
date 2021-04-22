package com.zhang.commons.activemq;

import com.zhang.entity.Resp;
import com.zhang.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class ActiveConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ActiveConsumer.class);

    @Autowired
    private SeckillService seckillService;
    @JmsListener(destination = "seckill.queue")
    public void receiveQueue(String msg) {
        String[] strings = msg.split(";"); // 信息保留的格式是 seckillid;userid
        logger.info("userId:" + Long.valueOf(strings[1]) + "消息队列开始处理");
        Resp resp = seckillService.startSeckillByDBOC(Long.valueOf(strings[0]), Long.valueOf(strings[1]), 1);
    }
}
