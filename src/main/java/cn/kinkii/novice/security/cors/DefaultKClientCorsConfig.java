package cn.kinkii.novice.security.cors;

public class DefaultKClientCorsConfig extends KClientCorsConfig {

  private static final String DEFAULT_CORS_ORIGINS = "*";

  public DefaultKClientCorsConfig() {
    super(DEFAULT_CORS_ORIGINS);
  }

}
