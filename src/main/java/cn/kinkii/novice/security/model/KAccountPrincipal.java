package cn.kinkii.novice.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RequiredArgsConstructor
@Getter
public class KAccountPrincipal implements Principal {
  private final String id;
  private final String username;
  private final Boolean isSupervisor;

  @Override
  public String getName() {
    return username;
  }
}
