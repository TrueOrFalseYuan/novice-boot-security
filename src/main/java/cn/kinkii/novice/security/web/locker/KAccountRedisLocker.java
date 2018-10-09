package cn.kinkii.novice.security.web.locker;

public class KAccountRedisLocker extends AbstractKAccountLocker {

  //TODO implement with redis
  public KAccountRedisLocker() {
    super();
    throw new IllegalStateException("The KAccountRedisLocker hasn't been implemented yet! Please use guava locker instead.");
  }

  public KAccountRedisLocker(int lockSeconds) {
    super(lockSeconds);
    throw new IllegalStateException("The KAccountRedisLocker hasn't been implemented yet! Please use guava locker instead.");
  }

  @Override
  public void lock(String username, Long lockTime) {}

  @Override
  public Boolean isLocked(String username) {
    return null;
  }
}