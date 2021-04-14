package com.zhang.service;

import com.zhang.entity.Resp;
import com.zhang.entity.Seckill;

import java.util.List;

public interface SeckillService {

    List<Seckill> getSeckillList();

    Seckill getById(long seckillId); // 查询单个秒杀记录

    Long getSeckillCount(long seckillId);

    void deleteSeckill(long seckillId);

    Resp startSeckill(long seckillId, long userId);

    Resp startSeckillByDBOC(long seckillId, long userId,long number);
}
