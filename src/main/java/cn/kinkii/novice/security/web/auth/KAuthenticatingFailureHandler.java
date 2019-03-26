package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.core.KAuthExceptionHandler;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KAuthenticatingFailureHandler implements AuthenticationFailureHandler {

    @Setter
    private List<KAuthFailureAdditionalHandler> additionalHandlers = new ArrayList<>();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        if(additionalHandlers != null) {
            additionalHandlers.forEach(handler -> handler.handle(request, response, e));
        }
        KAuthExceptionHandler.handle(request, response, e);
    }

    public KAuthenticatingFailureHandler addAdditionalHandler(KAuthFailureAdditionalHandler handler) {
        additionalHandlers.add(handler);
        return this;
    }

}

