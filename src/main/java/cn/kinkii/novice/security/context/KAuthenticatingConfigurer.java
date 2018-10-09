package cn.kinkii.novice.security.context;

import cn.kinkii.novice.security.access.KUrlAccessDecisionVoter;
import cn.kinkii.novice.security.cors.KClientCorsConfigurationSource;
import cn.kinkii.novice.security.crypto.KPasswordEncoderFactory;
import cn.kinkii.novice.security.rememberme.RememberKClientAuthFilter;
import cn.kinkii.novice.security.rememberme.RememberKClientServices;
import cn.kinkii.novice.security.service.KAccountService;
import cn.kinkii.novice.security.token.JwtTokenProcessor;
import cn.kinkii.novice.security.token.KRawTokenProcessor;
import cn.kinkii.novice.security.token.UuidTokenProcessor;
import cn.kinkii.novice.security.web.KAccessDeniedHandler;
import cn.kinkii.novice.security.web.KAuthenticationEntryPoint;
import cn.kinkii.novice.security.web.access.KAccessTokenProvider;
import cn.kinkii.novice.security.web.auth.*;
import cn.kinkii.novice.security.web.cache.GuavaKAccountCache;
import cn.kinkii.novice.security.web.cache.RedisKAccountCache;
import cn.kinkii.novice.security.web.counter.KAuthGuavaCounter;
import cn.kinkii.novice.security.web.counter.KAuthRedisCounter;
import cn.kinkii.novice.security.web.locker.KAccountGuavaLocker;
import cn.kinkii.novice.security.web.locker.KAccountRedisLocker;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnusedAssignment")
public class KAuthenticatingConfigurer {

    //==============================================================================
    protected final KAuthenticatingConfig _config;
    //==============================================================================
    protected KAccountService _accountService = null;
    protected AuthenticationManager _authenticationManager = null;
    //==============================================================================
    protected UserCache _accountCache = null;
    protected KRawTokenProcessor _tokenProcessor = null;
    protected RememberMeServices _rememberServices = null;

    //==============================================================================
    // init configurer
    //==============================================================================
    public KAuthenticatingConfigurer(KAuthenticatingConfig config) {
        this._config = config;
        this._accountCache = buildAccountCache(config);
        this._tokenProcessor = buildTokenProcessor(config);
    }

    protected static KRawTokenProcessor buildTokenProcessor(KAuthenticatingConfig config) {
        if (KAuthenticatingConfig.TOKEN_TYPE_JWT.equals(config.getTokenType())) {
            return new JwtTokenProcessor(config.getTokenJwt());
        } else if (KAuthenticatingConfig.TOKEN_TYPE_UUID.equals(config.getTokenType())) {
            return new UuidTokenProcessor(config.getTokenUuid());
        }
        throw new IllegalStateException("Unsupported token type! - " + config.getTokenType());
    }

    //==============================================================================
    // Spring-Security component
    //==============================================================================

    protected RememberMeServices buildRememberServices() {
        Assert.notNull(_tokenProcessor, "The token processor can't be null!");
        if (_rememberServices != null) {
            return _rememberServices;
        }
        return new RememberKClientServices(_tokenProcessor);
    }

    protected static UserCache buildAccountCache(KAuthenticatingConfig config) {
        if (KAuthenticatingConfig.CACHE_TYPE_GUAVA.equals(config.getCacheType())) {
            return new GuavaKAccountCache(config.getCacheGuava());
        } else if (KAuthenticatingConfig.CACHE_TYPE_REDIS.equals(config.getCacheType())) {
            return new RedisKAccountCache(config.getCacheRedis());
        }
        return new NullUserCache();
    }

    public KAuthenticatingConfigurer accountService(KAccountService accountService) {
        if (_accountService != null) {
            throw new IllegalStateException("The accountService has been set/loaded!");
        }
        this._accountService = accountService;
        return this;
    }

    public KAccountService currentAccountService() {
        if (_accountService == null) {
            throw new IllegalStateException("The KAccountService should be manual set!");
        }
        return _accountService;
    }

    public KAuthenticatingConfigurer authenticationManager(AuthenticationManager authenticationManager) {
        if (_authenticationManager != null) {
            throw new IllegalStateException("The AuthenticationManager has been set/loaded!");
        }
        this._authenticationManager = authenticationManager;
        return this;
    }

    public AuthenticationManager currentAuthenticationManager() {
        if (_authenticationManager == null) {
            throw new IllegalStateException("The AuthenticationManager should be manual set!");
        }
        return _authenticationManager;
    }

    protected AccessDecisionManager buildAccessDecisionManager() {
        return new AffirmativeBased(Arrays.asList(new WebExpressionVoter(), new KUrlAccessDecisionVoter().supervisorGranted(_config.isSupervisorGranted())));
    }

    protected CorsConfigurationSource buildCorsConfigurationSource() {
        return new KClientCorsConfigurationSource(_config.getCors());
    }

    //==============================================================================
    // filter
    //==============================================================================
    protected List<KFilter> buildFilters() {
        List<KFilter> filters = new ArrayList<>();
        filters.add(new KFilter(buildKAccountAuthFilter(), KFilter.Position.AFTER, AbstractPreAuthenticatedProcessingFilter.class));
        filters.add(new KFilter(buildKRefreshAuthFilter(), KFilter.Position.AFTER, AbstractPreAuthenticatedProcessingFilter.class));
        filters.add(new KFilter(buildRememberKClientAuthFilter(), KFilter.Position.AT, RememberMeAuthenticationFilter.class));
        return filters;
    }

    private RememberKClientAuthFilter buildRememberKClientAuthFilter() {
        return new RememberKClientAuthFilter(currentAuthenticationManager(), buildRememberServices());
    }


    private KAccountAuthFilter buildKAccountAuthFilter() {
        KAccountAuthFilter accountAuthFilter = new KAccountAuthFilter();
        accountAuthFilter.setRememberMeServices(buildRememberServices());
        accountAuthFilter.setAuthenticationManager(currentAuthenticationManager());
        return accountAuthFilter;
    }

    private KRefreshAuthFilter buildKRefreshAuthFilter() {
        KRefreshAuthFilter refreshAuthFilter = new KRefreshAuthFilter();
        refreshAuthFilter.setRememberMeServices(buildRememberServices());
        refreshAuthFilter.setAuthenticationManager(currentAuthenticationManager());
        return refreshAuthFilter;
    }

    //==============================================================================
    // provider
    //==============================================================================


    protected List<AuthenticationProvider> buildProviders() {
        List<AuthenticationProvider> providers = new ArrayList<>();

        providers.add(buildKAccountAuthProvider());
        providers.add(new KRefreshAuthProvider(currentAccountService(), _tokenProcessor).userCache(_accountCache));
        providers.add(new KAccessTokenProvider(currentAccountService(), _tokenProcessor).userCache(_accountCache));

        return providers;
    }

    private KAccountAuthProvider buildKAccountAuthProvider() {
        KAccountAuthProvider authProvider = new KAccountAuthProvider(currentAccountService()).userCache(_accountCache);

        initPasswordEncoder(authProvider);
        initLocker(authProvider);

        return authProvider;
    }

    private void initPasswordEncoder(KAccountAuthProvider authProvider) {
        KAccountAuthConfig authConfig = _config.getAuth();
        Assert.hasText(authConfig.getPasswordEncoder(), "The password encoder config can't be empty!");
        authProvider.setPasswordEncoder(KPasswordEncoderFactory.getInstance(authConfig.getPasswordEncoder()));
    }

    private void initLocker(KAccountAuthProvider authProvider) {
        KAccountAuthConfig authConfig = _config.getAuth();
        Assert.hasText(authConfig.getLockType(), "The lock type config can't be empty!");
        // locker
        Assert.notNull(authConfig.getLockSeconds(), "The lock seconds config can't be null!");
        // counter
        Assert.notNull(authConfig.getLockCountingSeconds(), "The lock counting seconds config can't be null!");
        Assert.notNull(authConfig.getLockFrom(), "The lock limit config can't be null!");

        if (KAccountAuthConfig.LOCKER_TYPE_NONE.equals(authConfig.getLockType())) {
            // with default KAccountIgnoredLocker and KAuthIgnoredCounter
        } else if (KAccountAuthConfig.LOCKER_TYPE_GUAVA.equals(authConfig.getLockType())) {
            authProvider.locker(new KAccountGuavaLocker(authConfig.getLockSeconds()))
                .failureCounter(new KAuthGuavaCounter(authConfig.getLockFrom(), authConfig.getLockCountingSeconds()));
        } else if (KAccountAuthConfig.LOCKER_TYPE_REDIS.equals(authConfig.getLockType())) {
            authProvider.locker(new KAccountRedisLocker(authConfig.getLockSeconds()))
                .failureCounter(new KAuthRedisCounter(authConfig.getLockFrom(), authConfig.getLockCountingSeconds()));
        } else {
            throw new IllegalArgumentException("Unsupported lock type! - " + authConfig.getLockType());
        }

    }

    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.cors()
            .configurationSource(buildCorsConfigurationSource())
            .and()
            .csrf()
            .disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .rememberMe()
            .rememberMeServices(buildRememberServices());

        if (_config.getPublicUrls() != null) {
            http.authorizeRequests().antMatchers(_config.getPublicUrls().toArray(new String[0])).permitAll();
        }
        http.authorizeRequests().anyRequest().authenticated().accessDecisionManager(buildAccessDecisionManager());
        http.exceptionHandling().accessDeniedHandler(buildAuthenticationFailureHandler()).authenticationEntryPoint(buildAuthenticationEntryPoint());

        List<KFilter> filters = buildFilters();
        if (filters != null) {
            filters.forEach(kFilter -> {
                switch (kFilter.getPosition()) {
                    case AT:
                        http.addFilterAt(kFilter.getFilter(), kFilter.getReferencePositionClass());
                        break;
                    case BEFORE:
                        http.addFilterBefore(kFilter.getFilter(), kFilter.getReferencePositionClass());
                        break;
                    case AFTER:
                        http.addFilterAfter(kFilter.getFilter(), kFilter.getReferencePositionClass());
                        break;
                    default:
                }
            });
        }
    }

    public void configureAuthenticationManagerBuilder(AuthenticationManagerBuilder auth) {
        List<AuthenticationProvider> providers = buildProviders();
        if (providers != null) {
            providers.forEach(auth::authenticationProvider);
        }
    }

    private KAuthenticationEntryPoint buildAuthenticationEntryPoint() {
        return new KAuthenticationEntryPoint();
    }

    private KAccessDeniedHandler buildAuthenticationFailureHandler() {
        return new KAccessDeniedHandler();
    }

    @Getter
    @Setter
    private static class KFilter {

        private Position position;

        private Filter filter;

        private Class<? extends Filter> referencePositionClass;

        public KFilter(Filter filter, Position position, Class<? extends Filter> referencePositionClass) {
            this.filter = filter;
            this.position = position;
            this.referencePositionClass = referencePositionClass;
        }

        public enum Position {
            BEFORE, AFTER, AT
        }
    }


}
