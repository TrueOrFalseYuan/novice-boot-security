package cn.kinkii.novice.security.token;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtTokenConfig extends KRawTokenConfig {

  private String tokenIssuer;
  private String signAlgorithm;
  private String signKey;

  @SuppressWarnings("WeakerAccess")
  public SignatureAlgorithm getSignatureAlgorithm() {
    if (signAlgorithm == null || signAlgorithm.trim().length() == 0) {
      return SignatureAlgorithm.NONE;
    } else {
      return SignatureAlgorithm.valueOf(signAlgorithm);
    }
  }

}
