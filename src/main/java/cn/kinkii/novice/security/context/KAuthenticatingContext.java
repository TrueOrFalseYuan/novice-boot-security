package cn.kinkii.novice.security.context;

import cn.kinkii.novice.security.crypto.KPasswordEncoderFactory;
import cn.kinkii.novice.security.token.JwtTokenProcessor;
import cn.kinkii.novice.security.token.KRawTokenProcessor;
import cn.kinkii.novice.security.token.UuidTokenProcessor;
import cn.kinkii.novice.security.web.KClientResponseBuilder;
import cn.kinkii.novice.security.web.auth.KAccountAuthConfig;
import cn.kinkii.novice.security.web.cache.GuavaKAccountCache;
import cn.kinkii.novice.security.web.cache.RedisKAccountCache;
import cn.kinkii.novice.security.web.counter.KAuthCounter;
import cn.kinkii.novice.security.web.counter.KAuthGuavaCounter;
import cn.kinkii.novice.security.web.counter.KAuthIgnoredCounter;
import cn.kinkii.novice.security.web.counter.KAuthRedisCounter;
import cn.kinkii.novice.security.web.locker.KAccountGuavaLocker;
import cn.kinkii.novice.security.web.locker.KAccountIgnoredLocker;
import cn.kinkii.novice.security.web.locker.KAccountLocker;
import cn.kinkii.novice.security.web.locker.KAccountRedisLocker;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

public class KAuthenticatingContext {

    //==============================================================================
    protected final KAuthenticatingConfig _config;
    //==============================================================================
    protected UserCache _accountCache;
    protected KRawTokenProcessor _tokenProcessor;
    protected PasswordEncoder _passwordEncoder;
    protected KAccountLocker _accountLocker;
    protected KAuthCounter _authCounter;
    //==============================================================================
    protected KClientResponseBuilder _responseBuilder;

    //==============================================================================
    // init context
    //==============================================================================
    public KAuthenticatingContext(KAuthenticatingConfig config) {
        this._config = config;
        this._accountCache = buildAccountCache(config);
        this._passwordEncoder = buildPasswordEncoder(config);
        this._tokenProcessor = buildTokenProcessor(config);
        this._accountLocker = buildAccountLocker(config);
        this._authCounter = buildAuthCounter(config);
        this._responseBuilder = buildResponseBuilder(this._tokenProcessor);
    }

    //==============================================================================
    // utils
    //==============================================================================
    protected static KRawTokenProcessor buildTokenProcessor(KAuthenticatingConfig config) {
        if (KAuthenticatingConfig.TOKEN_TYPE_JWT.equals(config.getTokenType())) {
            return new JwtTokenProcessor(config.getTokenJwt());
        } else if (KAuthenticatingConfig.TOKEN_TYPE_UUID.equals(config.getTokenType())) {
            return new UuidTokenProcessor(config.getTokenUuid());
        }
        throw new IllegalStateException("Unsupported token type! - " + config.getTokenType());
    }

    protected static UserCache buildAccountCache(KAuthenticatingConfig config) {
        if (KAuthenticatingConfig.CACHE_TYPE_GUAVA.equals(config.getCacheType())) {
            return new GuavaKAccountCache(config.getCacheGuava());
        } else if (KAuthenticatingConfig.CACHE_TYPE_REDIS.equals(config.getCacheType())) {
            return new RedisKAccountCache(config.getCacheRedis());
        }
        return new NullUserCache();
    }

    private static PasswordEncoder buildPasswordEncoder(KAuthenticatingConfig config) {
        KAccountAuthConfig authConfig = config.getAuth();
        Assert.hasText(authConfig.getPasswordEncoder(), "The password encoder config can't be empty!");
        return KPasswordEncoderFactory.getInstance(authConfig.getPasswordEncoder());
    }

    private static KAuthCounter buildAuthCounter(KAuthenticatingConfig config) {
        KAccountAuthConfig authConfig = config.getAuth();
        Assert.hasText(authConfig.getLockType(), "The lock type config can't be empty!");
        Assert.notNull(authConfig.getLockCountingSeconds(), "The lock counting seconds config can't be null!");
        Assert.notNull(authConfig.getLockFrom(), "The lock limit config can't be null!");

        if (KAccountAuthConfig.LOCKER_TYPE_NONE.equals(authConfig.getLockType())) {
            return new KAuthIgnoredCounter();
        } else if (KAccountAuthConfig.LOCKER_TYPE_GUAVA.equals(authConfig.getLockType())) {
            return new KAuthGuavaCounter(authConfig.getLockFrom(), authConfig.getLockCountingSeconds());
        } else if (KAccountAuthConfig.LOCKER_TYPE_REDIS.equals(authConfig.getLockType())) {
            return new KAuthRedisCounter(authConfig.getLockFrom(), authConfig.getLockCountingSeconds());
        } else {
            throw new IllegalArgumentException("Unsupported lock type! - " + authConfig.getLockType());
        }

    }

    private static KAccountLocker buildAccountLocker(KAuthenticatingConfig config) {
        KAccountAuthConfig authConfig = config.getAuth();
        Assert.hasText(authConfig.getLockType(), "The lock type config can't be empty!");
        Assert.notNull(authConfig.getLockSeconds(), "The lock seconds config can't be null!");

        if (KAccountAuthConfig.LOCKER_TYPE_NONE.equals(authConfig.getLockType())) {
            return new KAccountIgnoredLocker();
        } else if (KAccountAuthConfig.LOCKER_TYPE_GUAVA.equals(authConfig.getLockType())) {
            return new KAccountGuavaLocker(authConfig.getLockSeconds());
        } else if (KAccountAuthConfig.LOCKER_TYPE_REDIS.equals(authConfig.getLockType())) {
            return new KAccountRedisLocker(authConfig.getLockSeconds());
        } else {
            throw new IllegalArgumentException("Unsupported lock type! - " + authConfig.getLockType());
        }

    }

    private static KClientResponseBuilder buildResponseBuilder(KRawTokenProcessor tokenProcessor) {
        return new KClientResponseBuilder(tokenProcessor);
    }

    public KAuthenticatingConfig config() {
        return _config;
    }

    public KRawTokenProcessor tokenProcessor() {
        return _tokenProcessor;
    }

    public KClientResponseBuilder responseBuilder() {
        return _responseBuilder;
    }

    public UserCache accountCache() {
        return _accountCache;
    }

    public PasswordEncoder passwordEncoder() {
        return _passwordEncoder;
    }

    public KAccountLocker accountLocker() {
        return _accountLocker;
    }

    public KAuthCounter authCounter() {
        return _authCounter;
    }


}
