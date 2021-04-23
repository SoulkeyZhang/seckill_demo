package com.zhang.commons.distributedlock.redis;


import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: sec_skill_zhang
 * @Package: com.zhang.commons.distributedlock.redis
 * @ClassName: RedisLockUtil
 * @Author: ZJH
 * @Description: redis实现分布式锁
 * @Date: 2021/4/22 19:21
 */
public class RedisLockUtil {
    private static RedissonClient redissonClient;

    public void setRedissonClient(RedissonClient locker) {
        RedisLockUtil.redissonClient = locker;
    }

    /**
     * 加锁
     * @param lockKey
     * @return
     */
    public static RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 释放锁
     * @param lockKey 锁的key
     */
    public static void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    /**
     * 释放锁
     * @param lock 锁实例
     */
    public static void unlock(RLock lock) {
        lock.unlock();
    }

    /**
     * 加锁，并设置获取锁时长
     * @param lockKey
     * @param timeout 超时的时长，单位为秒
     * @return
     */
    public static RLock lock(String lockKey, long timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 尝试获取锁
     * @param lockKey
     * @param waitTime 最多等待的时间
     * @param leaseTime 上锁后，自动释放锁的时间，两个时间的单位都是秒
     * @return 是否成功获取锁
     */
    public static boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime,leaseTime,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }
}
