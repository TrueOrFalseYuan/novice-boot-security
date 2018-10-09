package cn.kinkii.novice.security.web.locker;

import java.util.Date;

public interface KAccountLocker {

  void lock(String username, Long lockTime);

  default void lock(String username) {
    lock(username, new Date().getTime());
  }

  Boolean isLocked(String username);


}
