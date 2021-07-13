package com.zhang.commons.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: sec_skill_zhang
 * @Package: com.zhang.commons.redis
 * @ClassName: RedisUtil
 * @Author: ZJH
 * @Description: redis缓存工具类，基于redisTemplate实现
 * @Date: 2021/7/13 11:05
 */
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private static final String KEY_PREFIX_VALUE = "seckill:key:";

    public boolean put(String k, Serializable v, long time) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            ValueOperations ops = redisTemplate.opsForValue();
            ops.set(key,v);
            if (time > 0)
                redisTemplate.expire(key,time, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            logger.error("key: " + k + ", value: "+ v +"缓存失败");
            e.printStackTrace();
        }
        return false;
    }

    public boolean put(String k, Serializable v) {
        return put(k,v,-1);
    }

    public boolean containsKey(String k) {
        try {
            return redisTemplate.hasKey(KEY_PREFIX_VALUE + k);
        } catch (Exception e) {
            logger.error("key: "+k+"判断失败！");
            e.printStackTrace();
        }
        return false;
    }

    public Object get(String k) {
        try{
            ValueOperations ops = redisTemplate.opsForValue();
            Object v = ops.get(KEY_PREFIX_VALUE + k);
            return v;
        } catch (Exception e) {
            logger.error("key: "+k+"获取缓存失败");
            e.printStackTrace();
        }
        return null;
    }

    public boolean remove(String k) {
        try {
            return redisTemplate.delete(KEY_PREFIX_VALUE+k);
        } catch (Exception e) {
            logger.error("key: "+k+"删除失败");
            e.printStackTrace();
        }
        return false;
    }

    public long incr(String k,long delta) {
        ValueOperations ops = redisTemplate.opsForValue();
        return ops.increment(KEY_PREFIX_VALUE + k, delta);
    }
}
