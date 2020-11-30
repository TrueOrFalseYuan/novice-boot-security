package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.model.KAccount;
import cn.kinkii.novice.security.service.KAccountNotFoundException;
import cn.kinkii.novice.security.service.KCodeInvalidException;
import cn.kinkii.novice.security.service.KCodeService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

public class KCodeAuthProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {

    protected final Log logger = LogFactory.getLog(getClass());

    // ~ Instance fields
    // ================================================================================================

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    @Getter
    @Setter
    private UserDetailsChecker authenticationChecks = new DefaultAuthenticationChecks();

    @Getter
    @Setter
    private KCodeService codeService;
    // ~ Methods
    // ========================================================================================================

    public KCodeAuthProvider(KCodeService codeService) {
        this.codeService = codeService;
    }

    public final void afterPropertiesSet() throws Exception {
        Assert.notNull(this.messages, "A message source must be set");
        Assert.notNull(this.codeService, "A codeService must be set");
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(KCodeAuthToken.class, authentication, "The authentication should be a KCodeAuthToken!");

        UserDetails user = retrieveUser((KCodeAuthToken) authentication);
        authenticationChecks.check(user);

        return createSuccessAuthentication(user, authentication);
    }

    protected Authentication createSuccessAuthentication(UserDetails principal, Authentication authentication) {
        Assert.isInstanceOf(KAccount.class, principal, "The principal should be a KAccount!");
        Assert.isInstanceOf(KCodeAuthToken.class, authentication, "The authentication should be a KCodeAuthToken!");

        KAccount kAccount = (KAccount) principal;
        KCodeAuthToken kCodeAuthToken = (KCodeAuthToken) authentication;

        return new KAuthenticatingSuccessToken(kAccount, kAccount.getAuthorities(), kCodeAuthToken);
    }

    protected final UserDetails retrieveUser(KCodeAuthToken codeAuthToken) throws AuthenticationException {
        try {
            // Determine code
            String code = (codeAuthToken.getPrincipal() == null) ? "" : codeAuthToken.getName();
            UserDetails loadedUser = this.getCodeService().exchangeUserByCode(code, codeAuthToken.getAdditionalParams());
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException("KCodeService returned null, which is an interface contract violation");
            }
            return loadedUser;
        } catch (KCodeInvalidException | KAccountNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public boolean supports(Class<?> authentication) {
        return (KCodeAuthToken.class.isAssignableFrom(authentication));
    }

    private class DefaultAuthenticationChecks implements UserDetailsChecker {
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                logger.debug("User account is locked");
                throw new LockedException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
            }

            if (!user.isEnabled()) {
                logger.debug("User account is disabled");
                throw new DisabledException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
            }

            if (!user.isAccountNonExpired()) {
                logger.debug("User account is expired");
                throw new AccountExpiredException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
            }
        }
    }

}
