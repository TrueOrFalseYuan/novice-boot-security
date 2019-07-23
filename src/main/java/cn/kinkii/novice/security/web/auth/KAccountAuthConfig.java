package cn.kinkii.novice.security.web.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KAccountAuthConfig {

  public static final String PWD_ENCODER_PLAINTEXT = "plain";
  public static final String PWD_ENCODER_BCRYPT = "bcrypt";
  public static final String PWD_ENCODER_SCRYPT = "scrypt";
  public static final String PWD_ENCODER_PBKDF2 = "pbkdf2";
  public static final String PWD_ENCODER_MD5 = "md5";
  public static final String PWD_ENCODER_SHA = "sha";
  public static final String PWD_ENCODER_SHA_256 = "sha-256";
  public static final String PWD_ENCODER_SHA_512 = "sha-512";
  public static final String PWD_ENCODER_STANDARD = "standard";

  private String passwordEncoder;

  public static final String LOCKER_TYPE_NONE = "none";
  public static final String LOCKER_TYPE_GUAVA = "guava";
  public static final String LOCKER_TYPE_REDIS = "redis";

  private String lockType;
  private Integer lockSeconds;
  private Integer lockFrom;
  private Integer lockCountingSeconds;

}
