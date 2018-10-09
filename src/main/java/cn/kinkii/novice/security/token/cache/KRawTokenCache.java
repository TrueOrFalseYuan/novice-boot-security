package cn.kinkii.novice.security.token.cache;

import cn.kinkii.novice.security.token.KRawTokenConfig;

public abstract class KRawTokenCache implements TokenCache {

    protected KRawTokenConfig tokenConfig;

    public KRawTokenCache(KRawTokenConfig tokenConfig) {
        this.tokenConfig = tokenConfig;
    }

}
