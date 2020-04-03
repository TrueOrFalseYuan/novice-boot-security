package cn.kinkii.novice.security.web.logout;

import cn.kinkii.novice.security.context.KAuthenticatingContext;
import cn.kinkii.novice.security.i18n.KSecurityMessageUtils;
import cn.kinkii.novice.security.token.KRawTokenData;
import cn.kinkii.novice.security.token.KRawTokenType;
import cn.kinkii.novice.security.token.exception.IllegalRawTokenException;
import cn.kinkii.novice.security.web.access.KAccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class KLogoutClearCacheHandler implements LogoutHandler {

    private KAuthenticatingContext context;

    public KLogoutClearCacheHandler(KAuthenticatingContext context) {
        this.context = context;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (!supports(authentication)) {
            throw new IllegalArgumentException("Unsupported authentication type! - " + authentication.getClass().getSimpleName());
        }

        KAccessToken accessToken = (KAccessToken) authentication;
        KRawTokenData tokenData = context.tokenProcessor().retrieve(accessToken.getRawToken(), KRawTokenType.ACCESS);
        if (tokenData == null) {
            log.debug("The token data can't be null after retrieving!");
            throw new IllegalRawTokenException(KSecurityMessageUtils.getExceptionMessage(IllegalRawTokenException.class));
        }
        if (!accessToken.getClientId().equals(tokenData.getClientId())) {
            log.debug("unmatched client id! requested http header: " + accessToken.getClientId() + " / parsed token: " + tokenData.getClientId());
            throw new IllegalRawTokenException(KSecurityMessageUtils.getExceptionMessage(IllegalRawTokenException.class));
        }

        context.accountCache().removeUserFromCache(tokenData.getUsername());
    }

    private static boolean supports(Authentication authentication) {
        return (KAccessToken.class.isAssignableFrom(authentication.getClass()));
    }
}
