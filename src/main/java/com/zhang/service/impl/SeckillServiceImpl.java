package com.zhang.service.impl;

import com.zhang.commons.dynamicquery.DynamicQuery;
import com.zhang.commons.dynamicquery.DynamicQueryImpl;
import com.zhang.commons.enums.SeckillStatEnum;
import com.zhang.entity.Resp;
import com.zhang.entity.Seckill;
import com.zhang.entity.SuccessKilled;
import com.zhang.repository.SeckillRepository;
import com.zhang.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class SeckillServiceImpl implements SeckillService {

    Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);


    @Autowired
    private DynamicQuery dynamicQuery;
    @Autowired
    private SeckillRepository seckillRepository;

    @Override
    public List<Seckill> getSeckillList() {
        return seckillRepository.findAll();
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillRepository.getOne(seckillId);
    }

    @Override
    public Long getSeckillCount(long seckillId) {
        String sql = "SELECT count(*) FROM success_killed WHERE seckill_id=?";
        Object object = dynamicQuery.nativeQueryObject(sql, seckillId);
        long number = ((Number) object).longValue();
        logger.warn("动态sql按照id获取到秒杀个数" + number);
        return number;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)  // 同时需要修改两个table，所以采用事务
    public void deleteSeckill(long seckillId) {
        String sql = "DELETE FROM success_killed WHERE seckill_id=?";
        dynamicQuery.nativeExecuteUpdate(sql,seckillId);
        sql = "UPDATE seckill SET number = 100 WHERE seckill_id=?";
        dynamicQuery.nativeExecuteUpdate(sql,seckillId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp startSeckill(long seckillId, long userId) { // 该实现会出现超卖的现象

        String sql = "SELECT number FROM seckill WHERE seckill_id=?";
        Object object = dynamicQuery.nativeQueryObject(sql, seckillId);  // 在多线程的情况下，这里获取到的个数，和下述的更新，不具有原子性
                                                                         // 所以下述判断的条件在事务执行时，可能已经不满足
        long number = ((Number) object).longValue();
        if (number > 0) {
            sql = "UPDATE seckill SET number = number - 1 WHERE seckill_id=?";
            dynamicQuery.nativeExecuteUpdate(sql,seckillId);  // 扣库存

            SuccessKilled killed = new SuccessKilled(); // 创建订单
            killed.setSeckillId(seckillId);
            killed.setUserId(userId);
            killed.setState((short) 0);
            killed.setCreateTime(new Timestamp(System.currentTimeMillis()));
            dynamicQuery.save(killed);  // 这里hibernate已经帮我们存数据库了，不需要下方执行sql

//            sql = "INSERT INTO success_killed (seckill_id, user_id, state, create_time) VALUES(?,?,?,?)";
//            Object[] params = new Object[]{seckillId, userId, (short)0, new Timestamp(System.currentTimeMillis())};
//            dynamicQuery.nativeExecuteUpdate(sql,params);

            return Resp.ok(SeckillStatEnum.SUCCESS);
        }
        return Resp.error(SeckillStatEnum.END);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Resp startSeckillByDBOC(long seckillId, long userId,long number) { // 基于数据库乐观锁的秒杀实现
                                                                              // ,但是该乐观锁实现是非公平锁的形式，不太符合业务逻辑
        Seckill kill = seckillRepository.getOne(seckillId);
        if (kill.getNumber() >= number) {
            String sql = "UPDATE seckill SET number = number - ?, version = version + 1 WHERE seckill_id=? AND version=?";
            int status = dynamicQuery.nativeExecuteUpdate(sql, new Object[]{number, seckillId, kill.getVersion()});
            if (status > 0) {
                SuccessKilled successKilled = new SuccessKilled();
                successKilled.setState((short) 0);
                successKilled.setUserId(userId);
                successKilled.setSeckillId(seckillId);
                successKilled.setCreateTime(new Timestamp(System.currentTimeMillis()));
                dynamicQuery.save(successKilled);
                return Resp.ok();
            } else {
                return Resp.error();
            }
        } else {
            return Resp.error();
        }
    }
}
