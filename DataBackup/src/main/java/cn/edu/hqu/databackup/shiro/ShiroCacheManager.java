package cn.edu.hqu.databackup.shiro;

import lombok.Data;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import cn.edu.hqu.databackup.utils.RedisUtils;

/**
 * @Author: egret
 */
@Data
public class ShiroCacheManager implements CacheManager {

    private long cacheLive;    // cache存活时间 秒

    private String cacheKeyPrefix; // cache前缀

    private RedisUtils redisUtils; // redis工具类

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return new ShiroCache<K, V>(cacheLive, cacheKeyPrefix, redisUtils);
    }
}
