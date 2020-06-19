package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.i18n.KSecurityMessageUtils;
import cn.kinkii.novice.security.model.KAccount;
import cn.kinkii.novice.security.service.KAccountService;
import cn.kinkii.novice.security.web.counter.KAuthCounter;
import cn.kinkii.novice.security.web.counter.KAuthIgnoredCounter;
import cn.kinkii.novice.security.web.counter.KAuthLimitExceededException;
import cn.kinkii.novice.security.web.locker.KAccountIgnoredLocker;
import cn.kinkii.novice.security.web.locker.KAccountLockedException;
import cn.kinkii.novice.security.web.locker.KAccountLocker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

public class KAccountAuthProvider extends DaoAuthenticationProvider {

    private Logger logger = LoggerFactory.getLogger(KAccountAuthProvider.class);

    private KAccountLocker locker = new KAccountIgnoredLocker();
    private KAuthCounter failureCounter = new KAuthIgnoredCounter();

    private boolean lockSupervisor = true;

    public KAccountAuthProvider(KAccountService accountService) {
        Assert.notNull(accountService, "The userDetailsService for KAccountAuthProvider can't be null!");
        super.setUserDetailsService(accountService);
    }

    public KAccountAuthProvider failureCounter(KAuthCounter failureCounter) {
        Assert.notNull(failureCounter, "The account authentication counter can't be null!");
        this.failureCounter = failureCounter;
        return this;
    }

    public KAccountAuthProvider locker(KAccountLocker locker) {
        Assert.notNull(locker, "The account locker can't be null!");
        this.locker = locker;
        return this;
    }

    public KAccountAuthProvider lockSupervisor(boolean lockSupervisor) {
        this.lockSupervisor = lockSupervisor;
        return this;
    }

    public KAccountAuthProvider userCache(UserCache cache) {
        setUserCache(cache);
        return this;
    }

    public KAccountAuthProvider passwordEncoder(PasswordEncoder passwordEncoder) {
        setPasswordEncoder(passwordEncoder);
        return this;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Assert.isTrue((authentication instanceof KAccountAuthToken), "The authentication should be a KAccountAuthToken!");
        Assert.isTrue((userDetails instanceof KAccount), "The UserDetails should be a KAccount!");

        KAccount kAccount = (KAccount) userDetails;

        if (userDetails.getPassword() == null) {
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        KAccountAuthToken accountToken = (KAccountAuthToken) authentication;
        if (locker.isLocked(accountToken.getUsername())) {
            logger.debug("The account '%s' has been locked at moment!");
            throw new KAccountLockedException(KSecurityMessageUtils.getExceptionMessage(KAccountLockedException.class));
        }
        if ((!kAccount.isSupervisor() && !kAccount.isAccountIgnoreLock()) ||
                (kAccount.isSupervisor() && !kAccount.isAccountIgnoreLock() && lockSupervisor)) {
            try {
                logger.debug(String.format("The account '%s' has been auth failure %d time(s)!", accountToken.getUsername(), failureCounter.get(accountToken.getUsername())));
                failureCounter.count(accountToken.getUsername());
            } catch (KAuthLimitExceededException e) {
                logger.debug(String.format("Locking the account '%s'!", accountToken.getUsername()));
                locker.lock(accountToken.getUsername());
                throw e;
            }
        }
        super.additionalAuthenticationChecks(userDetails, authentication);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        Assert.isTrue((principal instanceof KAccount), "The principal should be a KAccount!");
        Assert.isTrue((authentication instanceof KAccountAuthToken), "The authentication should be a KAccountAuthToken!");

        KAccount kAccount = (KAccount) principal;
        KAccountAuthToken accountToken = (KAccountAuthToken) authentication;

        failureCounter.clear(accountToken.getUsername());
        return new KAuthenticatingSuccessToken(kAccount, kAccount.getAuthorities(), accountToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (KAccountAuthToken.class.isAssignableFrom(authentication));
    }
}
