package cn.kinkii.novice.security.rememberme;

import cn.kinkii.novice.security.i18n.KSecurityMessageUtils;
import cn.kinkii.novice.security.model.KAccount;
import cn.kinkii.novice.security.model.KClient;
import cn.kinkii.novice.security.token.KRawTokenData;
import cn.kinkii.novice.security.token.KRawTokenProcessor;
import cn.kinkii.novice.security.token.KRawTokenType;
import cn.kinkii.novice.security.token.exception.MissingRawTokenException;
import cn.kinkii.novice.security.web.KClientContainer;
import cn.kinkii.novice.security.web.KClientDetails;
import cn.kinkii.novice.security.web.KHeader;
import cn.kinkii.novice.security.web.access.KAccessToken;
import cn.kinkii.novice.security.web.auth.KAuthenticatingSuccessToken;
import cn.kinkii.novice.security.web.auth.KRefreshAuthToken;
import cn.kinkii.novice.security.web.response.KClientResponse;
import cn.kinkii.novice.security.web.response.KRawTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RememberKClientServices implements RememberMeServices {

    private Logger logger = LoggerFactory.getLogger(RememberKClientServices.class);

    private final KRawTokenProcessor tokenProcessor;

    private static String obtainRawAccessToken(HttpServletRequest request) {
        String result = request.getHeader(KHeader.K_HEADER_ACCESS_TOKEN);
        if (!StringUtils.hasText(result)) {
            result = request.getParameter(KHeader.K_HEADER_ACCESS_TOKEN.toLowerCase());
        }
        return result;
    }

    public RememberKClientServices(KRawTokenProcessor tokenProcessor) {
        Assert.notNull(tokenProcessor, "The token processor can't be null!");
        this.tokenProcessor = tokenProcessor;
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        KClientDetails clientDetails = new KClientDetails(request);
        if (StringUtils.hasText(clientDetails.getClientId())) {
            String accessToken = obtainRawAccessToken(request);
            if (!StringUtils.hasText(accessToken)) {
                logger.debug("The access token can't be retrieved from the requested http header!");
                throw new MissingRawTokenException(KSecurityMessageUtils.getExceptionMessage(MissingRawTokenException.class));
            }
            logger.debug("Auto-login with access token - " + accessToken);

            KAccessToken kAccessToken = new KAccessToken(accessToken);
            kAccessToken.setKClientDetails(clientDetails);
            return kAccessToken;
        }
        return null;
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        if (!(auth instanceof KAuthenticatingSuccessToken)) {
            logger.info("Please use the RememberKClientServices with KAuthenticatingSuccessToken!");
            return;
        }
        
        KAuthenticatingSuccessToken successToken = (KAuthenticatingSuccessToken) auth;
        Assert.notNull(successToken.getPrincipal(), "The name of the successful token shouldn't be null!");
        Assert.notNull(successToken.getAuthorities(), "The authorities of the successful token shouldn't be empty!");

        Authentication authenticatingToken = successToken.getAuthenticatingToken();
        Assert.notNull(authenticatingToken, "The authenticating token shouldn't be null!");
        Assert.isInstanceOf(KClientContainer.class, authenticatingToken, "The authenticating token should be an instance of KClientContainer!");

        KClientContainer clientContainer = (KClientContainer) authenticatingToken;
        Assert.notNull(clientContainer.getKClientDetails(), "The client details of the successful token shouldn't be null!");
        Assert.notNull(clientContainer.getKClientDetails().getClient(), "The client details of the successful token shouldn't be null!");

        KClient client = clientContainer.getKClientDetails().getClient();
        KAccount kAccount = successToken.getAccountPrincipal();

        KRawTokenData tokenData = new KRawTokenData(kAccount.getId(), kAccount.getUsername(), client.getClientId());
        tokenData.setClientType(client.getClientType());
        tokenData.setClientVersion(client.getClientVersion());

        KClientResponse clientResponse = new KClientResponse(client.getClientId(), buildAccessToken(tokenData));

        if (authenticatingToken instanceof KRefreshAuthToken) {
            KRefreshAuthToken refreshAuthToken = (KRefreshAuthToken) authenticatingToken;
            if (tokenProcessor.needAutoRefresh(refreshAuthToken.getRawToken())) {
                clientResponse.setRefreshToken(buildRefreshToken(tokenData));
            }
        } else {
            clientResponse.setRefreshToken(buildRefreshToken(tokenData));
        }
        successToken.setKClientResponse(clientResponse);
    }

    private KRawTokenResponse buildAccessToken(KRawTokenData tokenData) {
        return new KRawTokenResponse(tokenProcessor.build(tokenData, KRawTokenType.ACCESS), tokenProcessor.getRawTokenConfig().getAccessExpires());
    }

    private KRawTokenResponse buildRefreshToken(KRawTokenData tokenData) {
        return new KRawTokenResponse(tokenProcessor.build(tokenData, KRawTokenType.REFRESH), tokenProcessor.getRawTokenConfig().getRefreshExpires());
    }

}