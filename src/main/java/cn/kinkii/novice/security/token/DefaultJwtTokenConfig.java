package cn.kinkii.novice.security.token;


import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

public class DefaultJwtTokenConfig extends JwtTokenConfig {

    private static final int DEFAULT_ACCESS_EXPIRE_SECONDS = 86400; //1 hour
    private static final int DEFAULT_REFRESH_EXPIRE_SECONDS = 604800; // 1 week
    private static final int DEFAULT_REFRESH_ON_REMAIN_SECONDS = 86400; //1 hour, won't take effect when less than or equal to zero

    private static final String DEFAULT_TOKEN_ISSUER = "K-Security";
    private static final String DEFAULT_SIGN_ALGORITHM = "HS512";
    private static final String DEFAULT_SIGN_KEY = new RandomStringGenerator.Builder().withinRange('0', 'z').filteredBy(CharacterPredicates.ASCII_ALPHA_NUMERALS).build().generate(16);

    public DefaultJwtTokenConfig() {
        setAccessExpires(DEFAULT_ACCESS_EXPIRE_SECONDS);
        setRefreshExpires(DEFAULT_REFRESH_EXPIRE_SECONDS);
        setRefreshOnRemains(DEFAULT_REFRESH_ON_REMAIN_SECONDS);

        setTokenIssuer(DEFAULT_TOKEN_ISSUER);
        setSignAlgorithm(DEFAULT_SIGN_ALGORITHM);
        setSignKey(DEFAULT_SIGN_KEY);
    }

}
