package cn.kinkii.novice.security.rememberme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RememberFailureHandler {

    void handle(HttpServletRequest request, HttpServletResponse response);
    
}
