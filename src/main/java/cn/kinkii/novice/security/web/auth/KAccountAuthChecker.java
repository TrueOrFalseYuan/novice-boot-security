package cn.kinkii.novice.security.web.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public interface KAccountAuthChecker {

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
    boolean check(UserDetails userDetails, KAccountAuthToken authentication) throws AuthenticationException;

}
