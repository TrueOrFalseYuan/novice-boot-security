package cn.kinkii.novice.security.web.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisKAccountCache extends KAccountCache {

    private static final String USER_CACHE_PREFIX = "K_CACHE_";
    private RedisTemplate<String, UserDetails> redisTemplate;

    public RedisKAccountCache(RedisConnectionFactory redisConnectionFactory) {
        this(new DefaultKAccountCacheConfig(), redisConnectionFactory);
    }

    public RedisKAccountCache(KAccountCacheConfig cacheConfig, RedisConnectionFactory redisConnectionFactory) {
        super(cacheConfig);
        Assert.notNull(redisConnectionFactory, "The redisConnectionFactory can't be null!");
        this.redisTemplate = buildRedisTemplate(redisConnectionFactory);
    }

    @Override
    public UserDetails getUserFromCache(String username) {
        try {
            return redisTemplate.opsForValue().get(buildCacheKey(username));
        } catch (Exception e) {
            log.error("Failed to get the cache for " + username + "! - " + e.getMessage());
            return null;
        }
    }

    @Override
    public void putUserInCache(UserDetails user) {
        try {
            redisTemplate.opsForValue().set(buildCacheKey(user.getUsername()), user, cacheConfig.getCacheSeconds(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to set the cache for " + user.getUsername() + "! - " + e.getMessage());
        }
    }

    @Override
    public void removeUserFromCache(String username) {
        try {
            redisTemplate.delete(buildCacheKey(username));
        } catch (Exception e) {
            log.error("Failed to remove the cache for " + username + "! - " + e.getMessage());
        }
    }

    private static RedisTemplate<String, UserDetails> buildRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, UserDetails> redisTemplate = new RedisTemplate<>();
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private static String buildCacheKey(String username) {
        return USER_CACHE_PREFIX + username;
    }
}
