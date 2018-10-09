package cn.kinkii.novice.security.web.access;

import cn.kinkii.novice.security.token.KRawTokenAuthentication;
import cn.kinkii.novice.security.token.KRawTokenType;
import lombok.Getter;

@Getter
public class KAccessToken extends KRawTokenAuthentication {

  public KAccessToken(String rawToken) {
    super(rawToken, KRawTokenType.ACCESS);
  }

}
