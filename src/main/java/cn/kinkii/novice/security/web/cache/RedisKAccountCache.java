package cn.kinkii.novice.security.web.cache;

import org.springframework.security.core.userdetails.UserDetails;

public class RedisKAccountCache extends KAccountCache {

  public RedisKAccountCache() {
    this(new DefaultRedisKAccountCacheConfig());
  }

  public RedisKAccountCache(RedisKAccountCacheConfig cacheConfig) {
    super(cacheConfig);
    throw new IllegalStateException("The RedisKAccountCache hasn't been implemented yet! Please use guava cache instead.");
  }

  @Override
  public UserDetails getUserFromCache(String username) {
    return null;
  }

  @Override
  public void putUserInCache(UserDetails user) {}

  @Override
  public void removeUserFromCache(String username) {}
}
