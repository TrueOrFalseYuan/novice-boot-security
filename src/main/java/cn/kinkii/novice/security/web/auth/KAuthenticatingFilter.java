package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.web.KClientAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

public abstract class KAuthenticatingFilter<T extends Authentication> extends KClientAuthenticationFilter<T> {

    protected KAuthenticatingSuccessHandler successHandler = new KAuthenticatingSuccessHandler();
    protected KAuthenticatingFailureHandler failureHandler = new KAuthenticatingFailureHandler();

    public KAuthenticatingFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
        this.setAuthenticationSuccessHandler(successHandler);
        this.setAuthenticationFailureHandler(failureHandler);
    }

    public void setAdditionalFailureHandlers(List<KAuthFailureAdditionalHandler> failureHandlers) {
        failureHandler.setAdditionalHandlers(failureHandlers);
    }

    public void setAdditionalSuccessHandlers(List<KAuthSuccessAdditionalHandler> successHandlers) {
         successHandler.setAdditionalHandlers(successHandlers);
    }

}
