package com.zhang.controller;

import com.zhang.entity.Resp;
import com.zhang.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Api(tags = "秒杀")
@RestController
@RequestMapping("/seckill")
public class SeckillController {

    private final static Logger logger = LoggerFactory.getLogger(SeckillController.class);

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize,corePoolSize+1
            ,10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000)); // 模拟并发下单

    @Autowired
    private SeckillService seckillService;

    /**
     *
     * @param seckillId
     * @return
     */
    @ApiOperation(value = "启动秒杀",nickname = "Soulkey")
    @PostMapping("/start")
    public Resp startSeckill(long seckillId) {
        int skillNum = 1000;
        final long seckill_id = seckillId;
        CountDownLatch latch = new CountDownLatch(skillNum);

        // 初始化秒杀过程，将秒杀项目还原
        seckillService.deleteSeckill(seckillId);
        logger.info("秒杀开始！");

        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = ()->{
                Resp resp = seckillService.startSeckill(seckill_id, userId);
                if (resp == null) {
                    logger.info("用户：" + userId + "请求太多，请稍后！");
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            logger.info("一共卖出了" + seckillCount + "件商品");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Resp.ok();
    }
    @ApiOperation(value = "数据库乐观锁秒杀",nickname = "Soulkey")
    @PostMapping("/startDBOCC")
    public Resp seckillByDBOC(long seckillId) {
        int skillNum = 1000;
        CountDownLatch latch = new CountDownLatch(skillNum);

        // 初始化秒杀过程，将秒杀项目还原
        seckillService.deleteSeckill(seckillId);
        logger.info("数据库乐观锁秒杀开始！");

        for (int i = 0; i < skillNum; i++) {
            long userId = i;

            Runnable task = ()->{
                Resp resp = seckillService.startSeckillByDBOC(seckillId, userId, 1);
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Long count = seckillService.getSeckillCount(seckillId);
            logger.info("一共秒杀出{}件商品", count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Resp.ok();
    }
}
