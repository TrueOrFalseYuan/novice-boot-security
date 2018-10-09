package cn.kinkii.novice.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

public class KPublicSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().configurationSource(request -> {
      CorsConfiguration cc = new CorsConfiguration();
      cc.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
      cc.setAllowedMethods(Collections.singletonList(CorsConfiguration.ALL));
      cc.setAllowedOrigins(Collections.singletonList(CorsConfiguration.ALL));
      cc.setAllowCredentials(true);
      cc.setMaxAge(3600L);
      return cc;
    }).and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests().anyRequest().permitAll();
  }

}
