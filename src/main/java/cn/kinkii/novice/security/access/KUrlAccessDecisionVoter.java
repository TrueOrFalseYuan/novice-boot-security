package cn.kinkii.novice.security.access;

import cn.kinkii.novice.security.model.KUrlAuthority;
import lombok.Getter;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public class KUrlAccessDecisionVoter implements AccessDecisionVoter<FilterInvocation> {

    private static final int ACCESS_GRANTED = 1; //the voter gives an affirmative answer
    private static final int ACCESS_DENIED = -1; //the voter gives a negative answer
    private static final int ACCESS_ABSTAIN = 0; //the voter abstains from voting

    @Getter
    private boolean isSupervisorAlwaysGranted = true;
    @Getter
    private boolean isAnonymousAllowed = false;

    public KUrlAccessDecisionVoter supervisorGranted(boolean granted) {
        this.isSupervisorAlwaysGranted = granted;
        return this;
    }

    public KUrlAccessDecisionVoter anonymousAllowed(boolean allowed) {
        this.isAnonymousAllowed = allowed;
        return this;
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation filterInvocation, Collection<ConfigAttribute> collection) {
        if (authentication instanceof KUrlAccessAuthentication) {
            if (authentication.isAuthenticated()) {
                return ACCESS_GRANTED;
            }
            KUrlAccessAuthentication accessAuthentication = (KUrlAccessAuthentication) authentication;
            if (isSupervisorAlwaysGranted && accessAuthentication.getAccountPrincipal().isSupervisor()) {
                return ACCESS_GRANTED;
            } else {
                HttpServletRequest request = filterInvocation.getHttpRequest();
                AntPathRequestMatcher urlMatcher;
                for (GrantedAuthority ga : accessAuthentication.getAuthorities()) {
                    if (ga instanceof KUrlAuthority) {
                        KUrlAuthority urlGA = (KUrlAuthority) ga;
                        urlMatcher = new AntPathRequestMatcher(urlGA.getUrl());
                        if (urlMatcher.matches(request)) {
                            if (urlGA.getMethod() == null || urlGA.getMethod().trim().length() == 0 || urlGA.getMethod().equals(request.getMethod())) {
                                return ACCESS_GRANTED;
                            }
                        }
                    }
                }
            }
            return ACCESS_DENIED;
        } else {
            if (authentication instanceof AnonymousAuthenticationToken) {
                return isAnonymousAllowed ? ACCESS_GRANTED : ACCESS_DENIED;
            } else {
                return ACCESS_ABSTAIN;
            }
        }

    }
}
