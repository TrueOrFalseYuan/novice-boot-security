package cn.kinkii.novice.security.web.counter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

@Slf4j
public class KAuthRedisCounter extends AbstractKAuthCachedCounter {

    private static final String USER_COUNTER_PREFIX = "K_COUNTER_";
    private RedisTemplate<String, Long> redisTemplate;

    public KAuthRedisCounter(RedisConnectionFactory redisConnectionFactory) {
        this(DEFAULT_COUNT_LIMIT, DEFAULT_COUNT_SECONDS, redisConnectionFactory);
    }

    public KAuthRedisCounter(int countLimit, int countSeconds, RedisConnectionFactory redisConnectionFactory) {
        super(countLimit, countSeconds);
        Assert.notNull(redisConnectionFactory, "The redisConnectionFactory can't be null!");
        this.redisTemplate = buildRedisTemplate(redisConnectionFactory);
    }

    @Override
    protected void setValue(String countKey, Long countValue) {
        try {
            log.debug("Counting for  " + countKey + " - " + countValue);
            redisTemplate.opsForValue().set(buildCounterKey(countKey), countValue, this.countSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to set count value for " + countKey + "! - " + e.getMessage());
        }
    }

    @Override
    protected Long getValue(String countKey) {
        try {
            return redisTemplate.opsForValue().get(buildCounterKey(countKey));
        } catch (Exception e) {
            log.error("Failed to get the authenticating count value for " + countKey + "! - " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public void clear(String countKey) {
        try {
            redisTemplate.delete(buildCounterKey(countKey));
        } catch (Exception e) {
            log.error("Failed to remove the authenticating count value for " + countKey + "! - " + e.getMessage());
        }
    }

    private static RedisTemplate<String, Long> buildRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private static String buildCounterKey(String username) {
        return USER_COUNTER_PREFIX + username;
    }
}
