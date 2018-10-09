package cn.kinkii.novice.security.web.cache;

import org.springframework.security.core.userdetails.UserCache;
import org.springframework.util.Assert;

@SuppressWarnings("WeakerAccess")
public abstract class KAccountCache implements UserCache {

    protected KAccountCacheConfig cacheConfig;

    public KAccountCache(KAccountCacheConfig cacheConfig) {
        Assert.isTrue(cacheConfig.getCacheSeconds() >= 0, "The caching seconds should be positive!");
        this.cacheConfig = cacheConfig;
    }

}
