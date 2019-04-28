package cn.kinkii.novice.security.rememberme;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RememberSuccessHandler {

    void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
}
