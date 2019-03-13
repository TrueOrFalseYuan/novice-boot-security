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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

@SuppressWarnings("WeakerAccess")
public class KAuthenticatingContext {

    //==============================================================================
    protected KAuthenticatingConfig _config;
    //==============================================================================
    protected UserCache _accountCache;
    protected KRawTokenProcessor _tokenProcessor;
    protected PasswordEncoder _passwordEncoder;
    protected KAccountLocker _accountLocker;
    protected KAuthCounter _authCounter;
    //==============================================================================
    protected RedisConnectionFactory _redisConnectionFactory = null;
    //==============================================================================
    protected KClientResponseBuilder _responseBuilder;
    //==============================================================================

    //==============================================================================
    // init context
    //==============================================================================
    public KAuthenticatingContext preset(RedisConnectionFactory redisConnectionFactory) {
        this._redisConnectionFactory = redisConnectionFactory;
        return this;
    }

    public KAuthenticatingContext init(KAuthenticatingConfig config) {
        this._config = config;
        this._accountCache = buildAccountCache();
        this._passwordEncoder = buildPasswordEncoder();
        this._tokenProcessor = buildTokenProcessor();
        this._accountLocker = buildAccountLocker();
        this._authCounter = buildAuthCounter();
        this._responseBuilder = buildResponseBuilder();
        return this;
    }

    //==============================================================================
    // utils
    //==============================================================================
    private KRawTokenProcessor buildTokenProcessor() {
        if (KAuthenticatingConfig.TOKEN_TYPE_JWT.equals(_config.getTokenType())) {
            return new JwtTokenProcessor(_config.getTokenJwt());
        } else if (KAuthenticatingConfig.TOKEN_TYPE_UUID.equals(_config.getTokenType())) {
            return new UuidTokenProcessor(_config.getTokenUuid());
        }
        throw new IllegalStateException("Unsupported token type! - " + _config.getTokenType());
    }

    private UserCache buildAccountCache() {
        if (KAuthenticatingConfig.CACHE_TYPE_GUAVA.equals(_config.getCacheType())) {
            return new GuavaKAccountCache(_config.getCacheConfig());
        } else if (KAuthenticatingConfig.CACHE_TYPE_REDIS.equals(_config.getCacheType())) {
            if (_redisConnectionFactory == null) {
                throw new IllegalArgumentException("Please set redisConnectionFactory first!");
            }
            return new RedisKAccountCache(_config.getCacheConfig(), _redisConnectionFactory);
        }
        return new NullUserCache();
    }

    private PasswordEncoder buildPasswordEncoder() {
        KAccountAuthConfig authConfig = _config.getAuth();
        Assert.hasText(authConfig.getPasswordEncoder(), "The password encoder config can't be empty!");
        return KPasswordEncoderFactory.getInstance(authConfig.getPasswordEncoder());
    }

    private KAuthCounter buildAuthCounter() {
        KAccountAuthConfig authConfig = _config.getAuth();
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

    private KAccountLocker buildAccountLocker() {
        KAccountAuthConfig authConfig = _config.getAuth();
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

    private KClientResponseBuilder buildResponseBuilder() {
        return new KClientResponseBuilder(_tokenProcessor);
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
