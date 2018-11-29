package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.model.KAccount;
import cn.kinkii.novice.security.service.KAccountService;
import cn.kinkii.novice.security.token.KRawTokenAuthProvider;
import cn.kinkii.novice.security.token.KRawTokenAuthentication;
import cn.kinkii.novice.security.token.KRawTokenProcessor;
import cn.kinkii.novice.security.token.KRawTokenType;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

public class KRefreshAuthProvider extends KRawTokenAuthProvider {

    public KRefreshAuthProvider(KAccountService accountService, KRawTokenProcessor processor) {
        super(accountService, processor);
    }

    @Override
    public KRawTokenType getCurrentTokenType() {
        return KRawTokenType.REFRESH;
    }

    @Override
    public Authentication buildSuccessAuthentication(KRawTokenAuthentication tokenAuthentication, KAccount authenticatedAccount) {
        return new KAuthenticatingSuccessToken(authenticatedAccount, authenticatedAccount.getAuthorities(), tokenAuthentication);
    }

    @Override
    public void setLoadFromUserCache(boolean loadFromUserCache) {
        Assert.isTrue(!loadFromUserCache, "Please don't try to load the userDetails from cache ! It will reload the authorities every successful refreshment!");
        super.setLoadFromUserCache(false);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (KRefreshAuthToken.class.isAssignableFrom(authentication));
    }
}