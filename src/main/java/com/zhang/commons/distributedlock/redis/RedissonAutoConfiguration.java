package com.zhang.commons.distributedlock.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @ProjectName: sec_skill_zhang
 * @Package: com.zhang.commons.distributedlock.redis
 * @ClassName: RedissonAutoConfiguration
 * @Author: ZJH
 * @Description: redisson自动装配类，用于设置单机（哨兵）模式，用于获取redisson工具类
 * @Date: 2021/4/23 13:35
 */
@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonAutoConfiguration {
    @Autowired
    private RedissonProperties redissonProperties;

    /**
     * 装配locker类，将其实例注入到工具类中
     * @param client
     * @return 返回工具类的实例
     */
    @Bean
    public RedisLockUtil redisLockUtil(RedissonClient client) {
        RedisLockUtil util = new RedisLockUtil();
        util.setRedissonClient(client);
        return util;
    }

    /**
     * 用于生成locker实例，从配置文件和配置类中获取配置
     * @return locker实例 redissonClient
     */
    @ConditionalOnProperty("redisson.address")
    @Bean
    RedissonClient redissonSingle() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(redissonProperties.getAddress())
                .setTimeout(redissonProperties.getTimeout())
                .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinIdleSize());

        if (StringUtils.hasLength(redissonProperties.getPassword())) {
            singleServerConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }
}
