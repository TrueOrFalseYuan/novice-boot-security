package cn.kinkii.novice.security.access;

import cn.kinkii.novice.security.model.KAccountPrincipal;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

@RequiredArgsConstructor
@Getter
@Setter
public class KUrlAccessAuthentication implements Authentication {

  @NonNull private KAccountPrincipal accountPrincipal;
  @NonNull private Collection<? extends GrantedAuthority> authorities;

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getDetails() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return accountPrincipal;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    Assert.isTrue(isAuthenticated, "KUrlAccessAuthentication is always authenticated!");
  }

  @Override
  public String getName() {
    return accountPrincipal.getUsername();
  }
}
