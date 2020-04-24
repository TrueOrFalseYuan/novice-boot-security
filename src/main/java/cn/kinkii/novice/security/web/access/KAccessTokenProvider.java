package cn.kinkii.novice.security.web.access;

import cn.kinkii.novice.security.access.KUrlAccessAuthentication;
import cn.kinkii.novice.security.model.KAccount;
import cn.kinkii.novice.security.service.KAccountService;
import cn.kinkii.novice.security.token.KRawTokenAuthProvider;
import cn.kinkii.novice.security.token.KRawTokenAuthentication;
import cn.kinkii.novice.security.token.KRawTokenProcessor;
import cn.kinkii.novice.security.token.KRawTokenType;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

public class KAccessTokenProvider extends KRawTokenAuthProvider {

    private List<KAccessSuccessHandler> successHandlers = new ArrayList<>();

    public void setSuccessHandlers(List<KAccessSuccessHandler> successHandlers) {
        this.successHandlers = successHandlers;
    }

    public KAccessTokenProvider addSuccessHandler(KAccessSuccessHandler handler) {
        if (successHandlers == null) {
            successHandlers = new ArrayList<>();
        }
        successHandlers.add(handler);
        return this;
    }

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
        if (successHandlers != null) {
            successHandlers.forEach(handler -> handler.handle(authenticatedAccount));
        }
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
