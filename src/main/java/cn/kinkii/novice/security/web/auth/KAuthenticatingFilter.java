package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.web.KClientAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

public abstract class KAuthenticatingFilter<T extends Authentication> extends KClientAuthenticationFilter<T> {

    protected KAuthenticatingSuccessHandler successHandler;
    protected KAuthenticatingFailureHandler failureHandler;

    public KAuthenticatingFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
        this.successHandler = new KAuthenticatingSuccessHandler();
        this.failureHandler = new KAuthenticatingFailureHandler();
        this.setAuthenticationSuccessHandler(successHandler);
        this.setAuthenticationFailureHandler(failureHandler);
    }

    public void setAdditionalFailureHandlers(List<KAuthFailureAdditionalHandler> handlers) {
        failureHandler.setAdditionalHandlers(handlers);
    }

    public void setAdditionalSuccessHandlers(List<KAuthSuccessAdditionalHandler> successHandlers) {
         successHandler.setAdditionalHandlers(successHandlers);
    }

}
