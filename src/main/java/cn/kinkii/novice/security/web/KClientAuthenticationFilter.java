package cn.kinkii.novice.security.web;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class KClientAuthenticationFilter<T extends Authentication> extends AbstractAuthenticationProcessingFilter {

    public KClientAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
        setAuthenticationDetailsSource(new KClientDetailsSource());
    }

    protected abstract T buildAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;

    protected void preCheckAuthentication(T builtAuthentication, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (builtAuthentication instanceof KClientContainer) {
            Assert.notNull(((KClientContainer) builtAuthentication).getKClientDetails().getClient(), "The client details should be initialized!");
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        T auth = buildAuthentication(request, response);
        Assert.notNull(auth, "The built authentication can't be null after call buildAuthentication() method!");
        if (auth instanceof KClientContainer) {
            ((KClientContainer) auth).setKClientDetails(((KClientDetailsSource) authenticationDetailsSource).buildDetails(request));
        }
        preCheckAuthentication(auth, request, response);
        return this.getAuthenticationManager().authenticate(auth);
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> detailsSource) {
        Assert.isTrue((detailsSource instanceof KClientDetailsSource),
            "The authenticationDetailsSource for KClientAuthenticationFilter should be an instance of KClientDetailsSource!"
        );
        super.setAuthenticationDetailsSource(detailsSource);
    }

}



