package cn.kinkii.novice.security.web.locker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

@Slf4j
public class KAccountRedisLocker extends AbstractKAccountLocker {

    private static final String USER_LOCKER_PREFIX = "K_LOCKER_";
    private RedisTemplate<String, Long> redisTemplate;

    //TODO implement with redis
    public KAccountRedisLocker(RedisConnectionFactory redisConnectionFactory) {
        this(DEFAULT_LOCK_SECONDS, redisConnectionFactory);
    }

    public KAccountRedisLocker(int lockSeconds, RedisConnectionFactory redisConnectionFactory) {
        super(lockSeconds);
        Assert.notNull(redisConnectionFactory, "The redisConnectionFactory can't be null!");
        this.redisTemplate = buildRedisTemplate(redisConnectionFactory);
    }

    @Override
    public void lock(String username, Long lockTime) {
        try {
            log.info("User " + username + " locked for " + lockTime + " seconds!");
            redisTemplate.opsForValue().set(buildLockerKey(username), lockTime, this.lockSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to lock the user " + username + "! - " + e.getMessage());
        }
    }

    @Override
    public void unlock(String username) {
        try {
            log.info("Unlock the user " + username + "!");
            redisTemplate.delete(buildLockerKey(username));
        } catch (Exception e) {
            log.error("Failed to unlock the user " + username + "! - " + e.getMessage());
        }

    }

    @Override
    public Boolean isLocked(String username) {
        try {
            return redisTemplate.opsForValue().get(buildLockerKey(username)) != null;
        } catch (Exception e) {
            log.error("Failed to check the lock state for " + username + "! - " + e.getMessage());
            return false;
        }
    }


    private static RedisTemplate<String, Long> buildRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private static String buildLockerKey(String username) {
        return USER_LOCKER_PREFIX + username;
    }
}
