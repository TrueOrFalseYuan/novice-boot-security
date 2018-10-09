package cn.kinkii.novice.security.token;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class KRawTokenConfig {
  private Integer accessExpires;
  private Integer refreshExpires;
  private Integer refreshOnRemains;
}
