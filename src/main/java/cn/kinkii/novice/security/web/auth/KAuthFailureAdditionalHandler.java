package cn.kinkii.novice.security.web.auth;

import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface KAuthFailureAdditionalHandler {

     void handle(HttpServletRequest request, HttpServletResponse response, AuthenticationException e);

}
