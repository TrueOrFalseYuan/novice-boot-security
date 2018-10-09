package cn.kinkii.novice.security.token.cache;

public interface TokenCache {

    String getTokenFromCache(String username, String type);

    void putTokenInCache(String username, String type, String token);

    void removeTokenFromCache(String username);

    void removeTokenFromCache(String username, String type);

}
