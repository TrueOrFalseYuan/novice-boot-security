package cn.kinkii.novice.security.web.counter;

public class KAuthIgnoredCounter implements KAuthCounter {

  @Override
  public void count(String accountKey) throws KAuthLimitExceededException {}

  @Override
  public Long get(String countKey) {
    return -1L;
  }

  @Override
  public void clear(String accountKey) {}

}
