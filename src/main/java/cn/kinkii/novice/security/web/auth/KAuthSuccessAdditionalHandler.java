package cn.kinkii.novice.security.web.auth;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface KAuthSuccessAdditionalHandler {

    void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

}
