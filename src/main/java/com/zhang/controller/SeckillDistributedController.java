package com.zhang.controller;

import com.zhang.commons.activemq.ActiveMQSender;
import com.zhang.entity.Resp;
import com.zhang.service.SeckillDistributedService;
import com.zhang.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Api(tags = "分布式秒杀")
@RestController
@RequestMapping("/seckillDistributed")
public class SeckillDistributedController {
    private static final Logger logger = LoggerFactory.getLogger(SeckillDistributedController.class);
    @Autowired
    private SeckillDistributedService seckillDistributedService;
    @Autowired
    private SeckillService seckillService;

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize,corePoolSize+1
            ,10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000));
    @Autowired
    private ActiveMQSender activeMQSender;
    @ApiOperation(value = "redis分布式锁秒杀", nickname = "Soulkey")
    @PostMapping("/startRedisLock")
    public Resp startSeckill(long seckillId) {
        seckillService.deleteSeckill(seckillId); // 初始化商品
        logger.info("开始redis分布式秒杀");
        int skillNum = 1000;
        CountDownLatch latch = new CountDownLatch(skillNum);
        for (int i = 0; i < skillNum; i++) {
            long userId = i;
            Runnable task = () -> {
                Resp resp = seckillDistributedService.startSeckillRedisLock(seckillId, userId);
                logger.info("用户id："+ userId + " " + resp.get("msg"));
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Long count = seckillService.getSeckillCount(seckillId);
            logger.info("一共秒杀出： " + count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Resp.ok();
    }
}
