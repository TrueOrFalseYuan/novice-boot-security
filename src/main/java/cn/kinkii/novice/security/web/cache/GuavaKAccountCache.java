package cn.kinkii.novice.security.web.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.concurrent.TimeUnit;

public class GuavaKAccountCache extends KAccountCache {

    private static final int DEFAULT_INITIAL_CAPACITY = 1000;

    private Cache<String, UserDetails> userCache;

    public GuavaKAccountCache() {
        this(new DefaultKAccountCacheConfig());
    }

    public GuavaKAccountCache(KAccountCacheConfig config) {
        super(config);
        userCache = CacheBuilder.newBuilder().initialCapacity(DEFAULT_INITIAL_CAPACITY).expireAfterWrite(config.getCacheSeconds(), TimeUnit.SECONDS).build();
    }

    @Override
    public UserDetails getUserFromCache(String username) {
        return userCache.getIfPresent(username);
    }

    @Override
    public void putUserInCache(UserDetails user) {
        userCache.put(user.getUsername(), user);
    }

    @Override
    public void removeUserFromCache(String username) {
        userCache.invalidate(username);
    }

}

