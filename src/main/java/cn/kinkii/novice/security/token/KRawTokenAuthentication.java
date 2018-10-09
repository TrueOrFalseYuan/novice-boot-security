package cn.kinkii.novice.security.token;

import cn.kinkii.novice.security.web.KClientContainer;
import cn.kinkii.novice.security.web.KClientDetails;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class KRawTokenAuthentication extends AbstractAuthenticationToken implements KClientContainer {

  private String rawToken;
  private KRawTokenType rawTokenType;

  @Setter private KClientDetails kClientDetails;

  public KRawTokenAuthentication(String rawToken, KRawTokenType rawTokenType) {
    super(null);
    this.rawToken = rawToken;
    this.rawTokenType = rawTokenType;
  }

  @Override
  public Object getCredentials() {
    return rawToken;
  }

  @Override
  public Object getPrincipal() {
    return rawToken;
  }
}

