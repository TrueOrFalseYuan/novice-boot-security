package cn.kinkii.novice.security.web.auth;

public class DefaultKAccountAuthConfig extends KAccountAuthConfig {

  private static final String DEFAULT_PASSWORD_ENCODER = PWD_ENCODER_BCRYPT;

  private static final String DEFAULT_LOCK_TYPE = LOCKER_TYPE_GUAVA;
  private static final int DEFAULT_LOCK_SECONDS = 3600;
  private static final int DEFAULT_LOCK_FROM = 10;
  private static final int DEFAULT_LOCK_COUNTING_SECONDS = 3600;

  public DefaultKAccountAuthConfig() {
    setPasswordEncoder(DEFAULT_PASSWORD_ENCODER);

    setLockType(DEFAULT_LOCK_TYPE);
    setLockSeconds(DEFAULT_LOCK_SECONDS);
    setLockFrom(DEFAULT_LOCK_FROM);
    setLockCountingSeconds(DEFAULT_LOCK_COUNTING_SECONDS);
  }
}
