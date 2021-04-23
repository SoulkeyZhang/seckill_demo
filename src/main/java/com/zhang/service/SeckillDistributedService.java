package com.zhang.service;

import com.zhang.entity.Resp;

public interface SeckillDistributedService {
    /**
     * 单个商品秒杀，redis分布式锁实现
     * @param seckillId 秒杀商品id
     * @param userId 用户id
     * @return
     */
    Resp startSeckillRedisLock(long seckillId, long userId);


}
