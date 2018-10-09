package cn.kinkii.novice.security.web.locker;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class KAccountGuavaLocker extends AbstractKAccountLocker {

  private static final int DEFAULT_INITIAL_CACHE = 1000;

  private Cache<String, Long> lockingKAccountCache;

  public KAccountGuavaLocker() {
    super();
    lockingKAccountCache = CacheBuilder.newBuilder().initialCapacity(DEFAULT_INITIAL_CACHE).expireAfterWrite(this.lockSeconds, TimeUnit.SECONDS).build();
  }

  public KAccountGuavaLocker(int lockSeconds) {
    super(lockSeconds);
    lockingKAccountCache = CacheBuilder.newBuilder().initialCapacity(DEFAULT_INITIAL_CACHE).expireAfterWrite(this.lockSeconds, TimeUnit.SECONDS).build();
  }

  @Override
  public void lock(String username, Long lockTime) {
    lockingKAccountCache.put(username, lockTime);
  }

  @Override
  public Boolean isLocked(String username) {
    return lockingKAccountCache.getIfPresent(username) != null;
  }

}
