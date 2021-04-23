package com.zhang.commons.distributedlock.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ProjectName: sec_skill_zhang
 * @Package: com.zhang.commons.distributedlock.redis
 * @ClassName: RedissonProperties
 * @Author: ZJH
 * @Description: redisson相关配置类
 * @Date: 2021/4/23 13:10
 */
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {
    private int timeout = 3000;

    private String address;

    private String password;

    private int connectionPoolSize = 64;

    private int connectionMinIdleSize = 10;

    private int slaveConnectionPoolSize = 250;

    private int masterConnectionPoolSize = 250;

    private String[] sentinelAddress;

    private String masterName;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public int getConnectionMinIdleSize() {
        return connectionMinIdleSize;
    }

    public void setConnectionMinIdleSize(int connectionMinIdleSize) {
        this.connectionMinIdleSize = connectionMinIdleSize;
    }

    public int getSlaveConnectionPoolSize() {
        return slaveConnectionPoolSize;
    }

    public void setSlaveConnectionPoolSize(int slaveConnectionPoolSize) {
        this.slaveConnectionPoolSize = slaveConnectionPoolSize;
    }

    public int getMasterConnectionPoolSize() {
        return masterConnectionPoolSize;
    }

    public void setMasterConnectionPoolSize(int masterConnectionPoolSize) {
        this.masterConnectionPoolSize = masterConnectionPoolSize;
    }

    public String[] getSentinelAddress() {
        return sentinelAddress;
    }

    public void setSentinelAddress(String[] sentinelAddress) {
        this.sentinelAddress = sentinelAddress;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }
}
