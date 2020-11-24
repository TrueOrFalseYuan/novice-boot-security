package cn.kinkii.novice.security.web.auth;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class AbstractKAccountAuthChecker implements KAccountAuthChecker {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    /**
     * @param userDetails user retrieved by KAccountService
     * @param authentication username/password requested
     * @return true to ignore other KAccountAuthCheckers & default password check by DaoAuthenticationProvider
     * @throws AuthenticationException any AuthenticationException
     *
     * you can throw exception, like
     * new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
     * to use default i18n message defined by spring-security
     */
    @Override
    public abstract boolean check(UserDetails userDetails, KAccountAuthToken authentication) throws AuthenticationException;

}
