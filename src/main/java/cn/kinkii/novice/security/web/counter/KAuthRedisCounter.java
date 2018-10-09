package cn.kinkii.novice.security.web.counter;

public class KAuthRedisCounter extends AbstractKAuthCachedCounter {

  //TODO implement with redis
  public KAuthRedisCounter() {
    super();
    throw new IllegalStateException("The KAuthRedisCounter hasn't been implemented yet! Please use guava counter instead.");
  }

  public KAuthRedisCounter(int countLimit, int countSeconds) {
    super(countLimit, countSeconds);
    throw new IllegalStateException("The KAuthRedisCounter hasn't been implemented yet! Please use guava counter instead.");
  }

  @Override
  protected void setValue(String countKey, Long countValue) {

  }

  @Override
  protected Long getValue(String countKey) {
    return null;
  }

  @Override
  public void clear(String countKey) {
    
  }
}
