package cn.kinkii.novice.security.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KClient {
  private String clientId;
  private String clientAddress;
  private String clientType;
  private String clientVersion;
}