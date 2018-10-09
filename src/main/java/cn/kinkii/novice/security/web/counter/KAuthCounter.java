package cn.kinkii.novice.security.web.counter;

public interface KAuthCounter {

  void count(String countKey) throws KAuthLimitExceededException;

  Long get(String countKey);

  void clear(String countKey);

}
