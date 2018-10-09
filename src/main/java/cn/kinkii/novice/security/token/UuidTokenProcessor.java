package cn.kinkii.novice.security.token;

import org.springframework.security.core.AuthenticationException;

public class UuidTokenProcessor implements KRawTokenProcessor {

  private UuidTokenConfig uuidTokenConfig;

  public UuidTokenProcessor() {
    this(new DefaultUuidTokenConfig());
  }

  public UuidTokenProcessor(UuidTokenConfig config) {
    throw new IllegalStateException("The UuidTokenProcessor hasn't been implemented yet! Please use jwt token instead.");
  }

  @Override
  public String build(KRawTokenData data, KRawTokenType type) {
    return null;
  }

  @Override
  public KRawTokenData retrieve(String rawToken, KRawTokenType type) throws AuthenticationException {
    return null;
  }

  @Override
  public boolean needAutoRefresh(String rawToken) {
    return false;
  }

  @Override
  public KRawTokenConfig getRawTokenConfig() {
    return uuidTokenConfig;
  }

}
