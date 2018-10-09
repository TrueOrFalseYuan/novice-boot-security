package cn.kinkii.novice.security.web.counter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class KAuthGuavaCounter extends AbstractKAuthCachedCounter {

  private static final int DEFAULT_INITIAL_CACHE = 1000;

  private Cache<String, Long> authCache;

  public KAuthGuavaCounter() {
    super();
    authCache = CacheBuilder.newBuilder().initialCapacity(DEFAULT_INITIAL_CACHE).expireAfterWrite(this.countSeconds, TimeUnit.SECONDS).build();
  }

  public KAuthGuavaCounter(int countLimit, int countSeconds) {
    super(countLimit, countSeconds);
    authCache = CacheBuilder.newBuilder().initialCapacity(DEFAULT_INITIAL_CACHE).expireAfterWrite(this.countSeconds, TimeUnit.SECONDS).build();
  }

  @Override
  public void clear(String countKey) {
    authCache.invalidate(countKey);
  }

  @Override
  public void setValue(String countKey, Long countValue) {
    authCache.put(countKey, countValue);
  }

  @Override
  public Long getValue(String countKey) {
    return authCache.getIfPresent(countKey);
  }
}
