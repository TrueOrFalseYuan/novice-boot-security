package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.token.KRawTokenAuthentication;
import cn.kinkii.novice.security.token.KRawTokenType;
import lombok.Getter;

@Getter
public class KRefreshAuthToken extends KRawTokenAuthentication {

  public KRefreshAuthToken(String rawToken) {
    super(rawToken, KRawTokenType.REFRESH);
  }

}
