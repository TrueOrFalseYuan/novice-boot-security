package cn.kinkii.novice.security.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class KClientResponse {
  private final String clientId;
  private final KRawTokenResponse accessToken;
  @Setter private KRawTokenResponse refreshToken;
}
