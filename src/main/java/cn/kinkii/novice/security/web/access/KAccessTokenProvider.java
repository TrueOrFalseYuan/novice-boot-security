package cn.kinkii.novice.security.web.access;

import cn.kinkii.novice.security.access.KUrlAccessAuthentication;
import cn.kinkii.novice.security.model.KAccount;
import cn.kinkii.novice.security.service.KAccountService;
import cn.kinkii.novice.security.token.KRawTokenAuthProvider;
import cn.kinkii.novice.security.token.KRawTokenAuthentication;
import cn.kinkii.novice.security.token.KRawTokenProcessor;
import cn.kinkii.novice.security.token.KRawTokenType;
import org.springframework.security.core.Authentication;

public class KAccessTokenProvider extends KRawTokenAuthProvider {

    public KAccessTokenProvider(KAccountService accountService, KRawTokenProcessor processor) {
        super(accountService, processor);
        setLoadFromUserCache(true);
    }

    @Override
    public KRawTokenType getCurrentTokenType() {
        return KRawTokenType.ACCESS;
    }

    @Override
    public Authentication buildSuccessAuthentication(KRawTokenAuthentication tokenAuthentication, KAccount authenticatedAccount) {
        return new KUrlAccessAuthentication(authenticatedAccount, authenticatedAccount.getAuthorities());
    }

    @Override
    public void setLoadFromUserCache(boolean loadFromUserCache) {
        if (!loadFromUserCache) {
            logger.warn("It's strongly recommend to load the user details from cache while user accessing!");
        }
        super.setLoadFromUserCache(loadFromUserCache);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (KAccessToken.class.isAssignableFrom(authentication));
    }

}
