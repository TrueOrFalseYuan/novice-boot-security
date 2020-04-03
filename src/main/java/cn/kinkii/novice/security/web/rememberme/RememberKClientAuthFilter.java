package cn.kinkii.novice.security.web.rememberme;

import cn.kinkii.novice.security.core.KAuthExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class RememberKClientAuthFilter extends RememberMeAuthenticationFilter {

    public RememberKClientAuthFilter(AuthenticationManager authenticationManager, RememberMeServices rememberMeServices) {
        super(authenticationManager, rememberMeServices);
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        try {
            KAuthExceptionHandler.handle(request, response, failed);
        } catch (IOException e) {
            log.error("Failed to response for unsuccessful authentication! - " + e.getMessage(), failed);
        }
    }
}
