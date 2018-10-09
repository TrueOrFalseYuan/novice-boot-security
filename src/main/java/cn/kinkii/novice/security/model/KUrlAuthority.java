package cn.kinkii.novice.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class KUrlAuthority implements GrantedAuthority {

  private final String url;
  private String method;

  @Override
  public String getAuthority() {
    return url + ((method != null && method.trim().length() > 0) ? ":" + method : "");
  }
}
