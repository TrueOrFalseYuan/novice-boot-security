package cn.kinkii.novice.security.web.locker;

public class KAccountIgnoredLocker implements KAccountLocker {

  @Override
  public void lock(String username, Long lockTime) {}

  @Override
  public Boolean isLocked(String username) {
    return false;
  }

  @Override
  public void unLock(String username) {}
}
