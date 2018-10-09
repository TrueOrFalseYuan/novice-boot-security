package cn.kinkii.novice.security.cors;

import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class KClientCorsConfig {
  private final String allowedOrigins;

  public List<String> getAllowedOrigins() {
    Assert.hasText(allowedOrigins.trim(), "The allowedOrigins can't be null!");
    List<String> result = new ArrayList<>();
    Arrays.asList(allowedOrigins.split(",")).forEach(s -> {result.add(s.trim());});
    return result;
  }
}
