package cn.kinkii.novice.security.web;

import cn.kinkii.novice.security.model.KAccount;
import cn.kinkii.novice.security.model.KClient;
import cn.kinkii.novice.security.token.KRawTokenData;
import cn.kinkii.novice.security.token.KRawTokenProcessor;
import cn.kinkii.novice.security.token.KRawTokenType;
import cn.kinkii.novice.security.web.response.KClientResponse;
import cn.kinkii.novice.security.web.response.KRawTokenResponse;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class KClientResponseBuilder {

    private final KRawTokenProcessor tokenProcessor;

    public KClientResponseBuilder(KRawTokenProcessor tokenProcessor) {
        Assert.notNull(tokenProcessor, "The token processor can't be null!");
        this.tokenProcessor = tokenProcessor;
    }

    public KClientResponse build(KAccount kAccount, HttpServletRequest request) {
        KClientDetails clientDetails = new KClientDetails(request);
        KClient client = clientDetails.getClient();
        if (client == null || !StringUtils.hasText(clientDetails.getClient().getClientId())) {
            client = new KClient(UUID.randomUUID().toString());
        }
        return build(kAccount, client);
    }

    public KClientResponse build(KAccount kAccount, KClient client) {
        KRawTokenData tokenData = new KRawTokenData(kAccount.getId(), kAccount.getUsername(), client.getClientId(), client.getClientType(), client.getClientVersion());
        return new KClientResponse(client.getClientId(), buildAccessToken(tokenData), buildRefreshToken(tokenData));
    }

    private KRawTokenResponse buildAccessToken(KRawTokenData tokenData) {
        return new KRawTokenResponse(tokenProcessor.build(tokenData, KRawTokenType.ACCESS), tokenProcessor.getRawTokenConfig().getAccessExpires());
    }

    private KRawTokenResponse buildRefreshToken(KRawTokenData tokenData) {
        return new KRawTokenResponse(tokenProcessor.build(tokenData, KRawTokenType.REFRESH), tokenProcessor.getRawTokenConfig().getRefreshExpires());
    }

}
