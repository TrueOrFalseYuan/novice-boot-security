package cn.kinkii.novice.security.web;

import org.springframework.security.authentication.AuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

public class KClientDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, KClientDetails> {

  public KClientDetails buildDetails(HttpServletRequest request) {
    return new KClientDetails(request);
  }

}
