package cn.kinkii.novice.security.token.cache;

public class NullTokenCache implements TokenCache {

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
