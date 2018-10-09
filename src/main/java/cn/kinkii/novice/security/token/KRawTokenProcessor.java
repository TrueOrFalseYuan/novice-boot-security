package cn.kinkii.novice.security.token;

import org.springframework.security.core.AuthenticationException;

public interface KRawTokenProcessor {

  String build(KRawTokenData data, KRawTokenType type);

  KRawTokenData retrieve(String rawToken, KRawTokenType type) throws AuthenticationException;

  boolean needAutoRefresh(String rawToken);

  KRawTokenConfig getRawTokenConfig();
}
