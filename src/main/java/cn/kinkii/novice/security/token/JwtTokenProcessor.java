package cn.kinkii.novice.security.token;

import cn.kinkii.novice.security.i18n.KSecurityMessageUtils;
import cn.kinkii.novice.security.token.exception.ExpiredRawTokenException;
import cn.kinkii.novice.security.token.exception.IllegalRawTokenException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public class JwtTokenProcessor implements KRawTokenProcessor {

  private JwtTokenConfig jwtTokenConfig;

  public JwtTokenProcessor() {
    this(new DefaultJwtTokenConfig());
  }

  public JwtTokenProcessor(JwtTokenConfig config) {
    this.jwtTokenConfig = config;
  }

  @Override
  public String build(KRawTokenData data, KRawTokenType type) {
    int expires = getExpires(type);

    Map<String, Object> claims = new HashMap<>();
    claims.put("type", type.name());
    claims.put("expires", expires);

    claims.put("userId", data.getUserId());

    claims.put("clientId", data.getClientId());
    if (StringUtils.hasText(data.getClientType())) {
      claims.put("clientType", data.getClientType());
    }
    if (StringUtils.hasText(data.getClientVersion())) {
      claims.put("clientVersion", data.getClientVersion());
    }

    claims.put("random", UUID.randomUUID());

    Calendar calNow = Calendar.getInstance();
    Calendar calExpiration = Calendar.getInstance();
    calExpiration.add(Calendar.SECOND, expires);

    return Jwts.builder()
               .setIssuer(jwtTokenConfig.getTokenIssuer())
               .setIssuedAt(calNow.getTime())
               .setExpiration(calExpiration.getTime())
               .signWith(jwtTokenConfig.getSignatureAlgorithm(), jwtTokenConfig.getSignKey())
               .setSubject(data.getUsername())
               .addClaims(claims)
               .compact();
  }

  @Override
  public KRawTokenData retrieve(String rawToken, KRawTokenType type) throws AuthenticationException {
    try {
      Jws<Claims> jwtClaims = Jwts.parser().setSigningKey(jwtTokenConfig.getSignKey()).parseClaimsJws(rawToken);

      String typeValue = jwtClaims.getBody().get("type", String.class);
      if (!StringUtils.hasText(typeValue) || !type.name().equals(typeValue)) {
        log.debug(String.format("Illegal %s raw token data!", type.name()));
        throw new IllegalRawTokenException(KSecurityMessageUtils.getExceptionMessage(IllegalRawTokenException.class));
      }

      String userId = jwtClaims.getBody().get("userId", String.class);
      String username = jwtClaims.getBody().getSubject();
      if (!StringUtils.hasText(userId) || !StringUtils.hasText(username)) {
        log.debug(String.format(String.format("Illegal user data of %s raw token!", type.name())));
        throw new IllegalRawTokenException(KSecurityMessageUtils.getExceptionMessage(IllegalRawTokenException.class));
      }
      String clientId = jwtClaims.getBody().get("clientId", String.class);
      if (!StringUtils.hasText(clientId)) {
        log.debug(String.format(String.format("Illegal client data of %s raw token!", type.name())));
        throw new IllegalRawTokenException(KSecurityMessageUtils.getExceptionMessage(IllegalRawTokenException.class));
      }

      KRawTokenData result = new KRawTokenData(userId, username, clientId);

      String clientType = jwtClaims.getBody().get("clientType", String.class);
      if (StringUtils.hasText(clientType)) {
        result.setClientType(clientType);
      }
      String clientVersion = jwtClaims.getBody().get("clientVersion", String.class);
      if (StringUtils.hasText(clientVersion)) {
        result.setClientVersion(clientVersion);
      }

      return result;
    } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
      log.debug(String.format("The %s raw token can't be parsed!", type.name()));
      throw new IllegalRawTokenException(KSecurityMessageUtils.getExceptionMessage(IllegalRawTokenException.class));
    } catch (ExpiredJwtException expired) {
      log.debug(String.format("The %s raw token has expired!", type.name()));
      throw new ExpiredRawTokenException(KSecurityMessageUtils.getExceptionMessage(ExpiredRawTokenException.class));
    }
  }

  @Override
  public boolean needAutoRefresh(String rawToken) {
    try {
      Jws<Claims> jwtClaims = Jwts.parser().setSigningKey(jwtTokenConfig.getSignKey()).parseClaimsJws(rawToken);
      if (!KRawTokenType.REFRESH.name().equals(jwtClaims.getBody().get("type", String.class))) {
        return false;
      }
      Long remainTimes = jwtClaims.getBody().getExpiration().getTime() - new Date().getTime();
      if (remainTimes < jwtTokenConfig.getRefreshOnRemains()) {
        return true;
      } else {
        return false;
      }
    } catch (Exception ex) {
      return true;
    }
  }

  @Override
  public KRawTokenConfig getRawTokenConfig() {
    return jwtTokenConfig;
  }


  private int getExpires(KRawTokenType type) {
    int expires = 0;
    if (KRawTokenType.ACCESS.equals(type)) {
      expires = jwtTokenConfig.getAccessExpires();
    } else if (KRawTokenType.REFRESH.equals(type)) {
      expires = jwtTokenConfig.getRefreshExpires();
    }
    return expires;
  }
}
