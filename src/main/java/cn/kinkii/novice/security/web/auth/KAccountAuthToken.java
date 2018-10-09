package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.web.KClientContainer;
import cn.kinkii.novice.security.web.KClientDetails;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class KAccountAuthToken extends UsernamePasswordAuthenticationToken implements KClientContainer {

  @Getter
  @Setter
  private KClientDetails kClientDetails;

  public KAccountAuthToken(String principal, String credentials) {
    super(principal, credentials);
  }

  public KAccountAuthToken(String principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
  }

  public String getUsername() {
    return (String) getPrincipal();
  }

  public String getPassword() {
    return (String) getCredentials();
  }

}
