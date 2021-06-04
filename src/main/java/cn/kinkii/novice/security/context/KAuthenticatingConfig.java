package cn.kinkii.novice.security.context;

import cn.kinkii.novice.security.cors.DefaultKClientCorsConfig;
import cn.kinkii.novice.security.cors.KClientCorsConfig;
import cn.kinkii.novice.security.token.DefaultJwtTokenConfig;
import cn.kinkii.novice.security.token.DefaultUuidTokenConfig;
import cn.kinkii.novice.security.token.JwtTokenConfig;
import cn.kinkii.novice.security.token.UuidTokenConfig;
import cn.kinkii.novice.security.web.auth.DefaultKAccountAuthConfig;
import cn.kinkii.novice.security.web.auth.KAccountAuthConfig;
import cn.kinkii.novice.security.web.cache.DefaultKAccountCacheConfig;
import cn.kinkii.novice.security.web.cache.KAccountCacheConfig;
import cn.kinkii.novice.security.web.locale.DefaultKLocaleConfig;
import cn.kinkii.novice.security.web.locale.KLocaleConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class KAuthenticatingConfig {

    public static final String CONFIG_PREFIX = "novice.k-security";

    public static final String TOKEN_TYPE_JWT = "jwt";
    public static final String TOKEN_TYPE_UUID = "uuid";

    public static final String CACHE_TYPE_GUAVA = "guava";
    public static final String CACHE_TYPE_REDIS = "redis";

    private Boolean authByAccount = true;
    private Boolean authByCode = false;

    private String tokenType = TOKEN_TYPE_JWT;
    private JwtTokenConfig tokenJwt = new DefaultJwtTokenConfig();
    private UuidTokenConfig tokenUuid = new DefaultUuidTokenConfig();

    private String cacheType = CACHE_TYPE_GUAVA;
    private KAccountCacheConfig cacheConfig = new DefaultKAccountCacheConfig();

    private KAccountAuthConfig auth = new DefaultKAccountAuthConfig();
    private KClientCorsConfig cors = new DefaultKClientCorsConfig();

    private boolean supervisorGranted = true; // grant supervisor all permission
    private boolean anonymousAllowed = false; // allow anonymous user to access
    private List<String> publicUrls = new ArrayList<>();

    private KLocaleConfig locale = new DefaultKLocaleConfig();

}
