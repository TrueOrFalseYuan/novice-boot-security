package cn.kinkii.novice.security.token;

import cn.kinkii.novice.security.i18n.KSecurityMessageUtils;
import cn.kinkii.novice.security.model.KAccount;
import cn.kinkii.novice.security.service.KAccountService;
import cn.kinkii.novice.security.token.exception.IllegalRawTokenException;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;

public abstract class KRawTokenAuthProvider implements AuthenticationProvider {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private KRawTokenProcessor tokenProcessor;
    private KAccountService accountService;

    private UserDetailsChecker authenticationChecks = new KAccountAuthenticationChecks();

    @Setter
    private boolean loadFromUserCache = false;
    @Setter
    private UserCache userCache = new NullUserCache();

    public KRawTokenAuthProvider(KAccountService accountService, KRawTokenProcessor processor) {
        Assert.notNull(accountService, "The account service can't be null!");
        Assert.notNull(processor, "The token processor can't be null!");

        this.accountService = accountService;
        this.tokenProcessor = processor;
    }

    public KRawTokenAuthProvider userCache(UserCache cache) {
        setUserCache(cache);
        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public abstract KRawTokenType getCurrentTokenType();

    @SuppressWarnings("WeakerAccess")
    public abstract Authentication buildSuccessAuthentication(KRawTokenAuthentication tokenAuthentication, KAccount authenticatedAccount);

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");
        if (!supports(authentication.getClass())) {
            return null;
        } else {
            KRawTokenAuthentication tokenAuthentication = (KRawTokenAuthentication) authentication;
            KRawTokenData tokenData = tokenProcessor.retrieve(tokenAuthentication.getRawToken(), getCurrentTokenType());
            if (tokenData == null) {
                logger.debug("The token data can't be null after retrieving!");
                throw new IllegalRawTokenException(KSecurityMessageUtils.getExceptionMessage(IllegalRawTokenException.class));
            }
            if (!tokenAuthentication.getClientId().equals(tokenData.getClientId())) {
                logger.debug("unmatched client id! requested http header: " + tokenAuthentication.getClientId() + " / parsed token: " + tokenData.getClientId());
                throw new IllegalRawTokenException(KSecurityMessageUtils.getExceptionMessage(IllegalRawTokenException.class));
            }
            UserDetails currentUser = null;
            if (loadFromUserCache) {
                currentUser = userCache.getUserFromCache(tokenData.getUsername());
            }
            if (currentUser == null) {
                currentUser = accountService.loadUserByUsername(tokenData.getUsername());
                userCache.putUserInCache(currentUser);
            }
            authenticationChecks.check(currentUser);

            return buildSuccessAuthentication(tokenAuthentication, (KAccount) currentUser);
        }
    }

    private class KAccountAuthenticationChecks implements UserDetailsChecker {

        protected MessageSourceAccessor springMessages = SpringSecurityMessageSource.getAccessor();

        public void check(UserDetails user) {
            Assert.isInstanceOf(KAccount.class, user, "The user detail for authentication should be an instance of KAccount!");

            KAccount account = (KAccount) user;
            if (account.isAccountLocked()) {
                logger.debug("User account is locked");
                throw new LockedException(springMessages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
            }
            if (!account.isEnabled()) {
                logger.debug("User account is disabled");
                throw new DisabledException(springMessages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
            }
            if (account.isAccountExpired()) {
                logger.debug("User account is expired");
                throw new AccountExpiredException(springMessages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
            }
        }
    }
}
