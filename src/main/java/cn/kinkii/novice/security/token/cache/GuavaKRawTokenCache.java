package cn.kinkii.novice.security.token.cache;

import cn.kinkii.novice.security.token.KRawTokenConfig;

public class GuavaKRawTokenCache extends KRawTokenCache {

    public GuavaKRawTokenCache(KRawTokenConfig tokenConfig) {
        super(tokenConfig);
    }

    @Override
    public String getTokenFromCache(String username, String type) {
        return null;
    }

    @Override
    public void putTokenInCache(String username, String type, String token) {

    }

    @Override
    public void removeTokenFromCache(String username) {

    }

    @Override
    public void removeTokenFromCache(String username, String type) {

    }
}
