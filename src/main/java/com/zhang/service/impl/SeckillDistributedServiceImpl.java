package com.zhang.service.impl;

import com.zhang.commons.distributedlock.redis.RedisLockUtil;
import com.zhang.commons.dynamicquery.DynamicQuery;
import com.zhang.entity.Resp;
import com.zhang.entity.SuccessKilled;
import com.zhang.service.SeckillDistributedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


import java.sql.Timestamp;
import java.util.Date;

/**
 * @ProjectName: sec_skill_zhang
 * @Package: com.zhang.service.impl
 * @ClassName: SeckillDistributedServiceImpl
 * @Author: ZJH
 * @Description:
 * @Date: 2021/4/22 18:59
 */
@Service
public class SeckillDistributedServiceImpl implements SeckillDistributedService {

    @Autowired
    private DynamicQuery dynamicQuery;
    /**
     * 单个商品秒杀，redis分布式锁实现。会出现超卖现象，库存正常减少，但是下单数会超出
     *
     * @param seckillId 秒杀商品id
     * @param userId    用户id
     * @return
     */
    @Override
    @Transactional
    public Resp startSeckillRedisLock(long seckillId, long userId) {

        boolean res = false;
        int i = 0;
        try {
            res = RedisLockUtil.tryLock(seckillId + "", 3, 20);
            if (res) {
                String sql = "SELECT number FROM seckill WHERE seckill_id=?";
                Object o = dynamicQuery.nativeQueryObject(sql, seckillId);
                long number = ((Number) o).longValue();
                if (number > 0) {
                    SuccessKilled successKilled = new SuccessKilled();
                    successKilled.setCreateTime(new Timestamp(new Date().getTime()));
                    successKilled.setSeckillId(seckillId);
                    successKilled.setUserId(userId);
                    successKilled.setState((short) 0);
                    dynamicQuery.save(successKilled);
                    sql = "UPDATE seckill SET number = number - 1 WHERE seckill_id=? AND number > 0";
                    i = dynamicQuery.nativeExecuteUpdate(sql, seckillId);
                    if (i == 0)
                        throw new RuntimeException("防止超卖");
                } else {
                    return Resp.error("商品已经卖完");
                }
            }else {
                return Resp.error("当前下单人数过多，操作失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 购买失败，事务回滚
            return Resp.error("购买失败");
        } finally { // 由于sql可能异常推出，所以需要在finally中释放锁
            if (res) {
                RedisLockUtil.unlock(seckillId+""); // 这里释放锁的时候，可能sql还没有更新number，导致下一个请求获取锁后得到的number是错误的数据，导致了被错误的成功下单
            }
        }


        return Resp.ok("购买成功");
    }
}
