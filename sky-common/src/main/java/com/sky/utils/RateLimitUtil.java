package com.sky.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * IP限流工具类
 * 基于 Guava 令牌桶算法
 */
@Component
public class RateLimitUtil {

    // 缓存：Key=IP地址, Value=该IP对应的限流器
    // 设置过期时间，防止长期不访问的IP占用内存
    private final LoadingCache<String, RateLimiter> ipRequestCaches = CacheBuilder.newBuilder()
            .maximumSize(1000) // 最多保存1000个IP状态
            .expireAfterAccess(1, TimeUnit.HOURS) // 1小时不访问自动清理
            .build(new CacheLoader<String, RateLimiter>() {
                @Override
                public RateLimiter load(String key) {
                    // 新IP产生时，创建限流器
                    // 0.5 表示每秒 0.5 个令牌（即 2秒 1 次请求）
                    // 如果你想每秒 1 次，就改成 1.0
                    return RateLimiter.create(0.5);
                }
            });

    /**
     * 尝试获取访问许可
     * @param ip 客户端IP
     * @return true=允许访问, false=被限流
     */
    public boolean tryAcquire(String ip) {
        try {
            return ipRequestCaches.get(ip).tryAcquire();
        } catch (ExecutionException e) {
            // 如果缓存加载失败，为了不影响用户，默认放行
            return true;
        }
    }
}