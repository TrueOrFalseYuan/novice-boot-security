package cn.kinkii.novice.security.web.cache;

public class DefaultKAccountCacheConfig extends KAccountCacheConfig {

    private static final long DEFAULT_CACHE_SECONDS = 3600; // 1 hour

    public DefaultKAccountCacheConfig() {
        setCacheSeconds(DEFAULT_CACHE_SECONDS);
    }

}
