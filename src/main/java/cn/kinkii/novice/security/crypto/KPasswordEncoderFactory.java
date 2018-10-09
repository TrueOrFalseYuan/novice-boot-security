package cn.kinkii.novice.security.crypto;

import cn.kinkii.novice.security.web.auth.KAccountAuthConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public class KPasswordEncoderFactory {

  private static final Map<String, PasswordEncoder> PASSWORD_ENCODERS = new HashMap<>();

  static {
    PASSWORD_ENCODERS.put(KAccountAuthConfig.PWD_ENCODER_BCRYPT, new BCryptPasswordEncoder());
    PASSWORD_ENCODERS.put(KAccountAuthConfig.PWD_ENCODER_SCRYPT, new SCryptPasswordEncoder());
    PASSWORD_ENCODERS.put(KAccountAuthConfig.PWD_ENCODER_PBKDF2, new Pbkdf2PasswordEncoder());

    PASSWORD_ENCODERS.put(KAccountAuthConfig.PWD_ENCODER_PLAINTEXT, NoOpPasswordEncoder.getInstance());
    PASSWORD_ENCODERS.put(KAccountAuthConfig.PWD_ENCODER_MD5, new MessageDigestPasswordEncoder("MD5"));
    PASSWORD_ENCODERS.put(KAccountAuthConfig.PWD_ENCODER_SHA, new MessageDigestPasswordEncoder("SHA-1"));
    PASSWORD_ENCODERS.put(KAccountAuthConfig.PWD_ENCODER_SHA_256, new MessageDigestPasswordEncoder("SHA-256"));
    PASSWORD_ENCODERS.put(KAccountAuthConfig.PWD_ENCODER_SHA_512, new MessageDigestPasswordEncoder("SHA-512"));
    PASSWORD_ENCODERS.put(KAccountAuthConfig.PWD_ENCODER_STANDARD, new StandardPasswordEncoder());
  }


  private KPasswordEncoderFactory() {
  }

  public static PasswordEncoder getInstance(String encoderId) {
    return new DelegatingPasswordEncoder(encoderId, PASSWORD_ENCODERS);
  }


}
