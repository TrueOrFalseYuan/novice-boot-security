package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.core.KAuthExceptionHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class KAuthenticatingFailureHandler implements AuthenticationFailureHandler {

    private List<KAuthFailureAdditionalHandler> additionalHandlers = new ArrayList<>();

    public void setAdditionalHandlers(List<KAuthFailureAdditionalHandler> additionalHandlers) {
        this.additionalHandlers = additionalHandlers;
    }

    public KAuthenticatingFailureHandler addAdditionalHandler(KAuthFailureAdditionalHandler handler) {
        if (additionalHandlers == null) {
            additionalHandlers = new ArrayList<>();
        }
        additionalHandlers.add(handler);
        return this;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        if (additionalHandlers != null) {
            additionalHandlers.forEach(handler -> handler.handle(request, response, e));
        }
        KAuthExceptionHandler.handle(request, response, e);
    }

}

