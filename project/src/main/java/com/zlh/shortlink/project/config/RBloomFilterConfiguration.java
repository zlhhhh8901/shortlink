package com.zlh.shortlink.project.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置
 */
@Configuration
public class RBloomFilterConfiguration {

    /**
     * 防止用户重复创建短链接的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> shortLinkCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter =
                redissonClient.getBloomFilter("shortLinkCreateCachePenetrationBloomFilter"); //布隆过滤器名称
        cachePenetrationBloomFilter.tryInit(1000000L, 0.001);
        return cachePenetrationBloomFilter;
    }
}