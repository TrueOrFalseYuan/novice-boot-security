package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.web.KClientAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.RequestMatcher;

public abstract class KAuthenticatingFilter<T extends Authentication> extends KClientAuthenticationFilter<T> {

    public KAuthenticatingFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
        this.setAuthenticationSuccessHandler(new KAuthenticatingSuccessHandler());
        this.setAuthenticationFailureHandler(new KAuthenticatingFailureHandler());
    }

}
