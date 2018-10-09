package cn.kinkii.novice.security.web.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class KRawTokenResponse {
  private final String token;
  private final Integer expires;
}
