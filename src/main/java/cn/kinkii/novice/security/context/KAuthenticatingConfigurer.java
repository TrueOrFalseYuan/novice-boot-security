package cn.kinkii.novice.security.context;

import cn.kinkii.novice.security.access.KUrlAccessDecisionVoter;
import cn.kinkii.novice.security.cors.KClientCorsConfigurationSource;
import cn.kinkii.novice.security.service.KAccountService;
import cn.kinkii.novice.security.service.KCodeService;
import cn.kinkii.novice.security.web.KAccessDeniedHandler;
import cn.kinkii.novice.security.web.KAuthenticationEntryPoint;
import cn.kinkii.novice.security.web.access.KAccessSuccessHandler;
import cn.kinkii.novice.security.web.access.KAccessTokenProvider;
import cn.kinkii.novice.security.web.auth.*;
import cn.kinkii.novice.security.web.locale.KLocaleContextRequestFilter;
import cn.kinkii.novice.security.web.locale.KLocaleContextResolver;
import cn.kinkii.novice.security.web.logout.KLogoutClearCacheHandler;
import cn.kinkii.novice.security.web.logout.KLogoutFilter;
import cn.kinkii.novice.security.web.logout.KLogoutSuccessHandler;
import cn.kinkii.novice.security.web.rememberme.RememberKClientAuthFilter;
import cn.kinkii.novice.security.web.rememberme.RememberKClientServices;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
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
    protected KAuthenticatingContext _context = null;
    //==============================================================================
    protected KAccountService _accountService = null;
    protected KCodeService _codeService = null;
    protected AuthenticationManager _authenticationManager = null;
    //==============================================================================
    protected RememberMeServices _rememberServices = null;
    //==============================================================================
    protected List<KAccountAuthChecker> _accountAuthCheckers;
    protected List<KAuthSuccessAdditionalHandler> _accountAuthSuccessHandlers;
    protected List<KAuthFailureAdditionalHandler> _accountAuthFailureHandlers;
    //==============================================================================
    protected List<KAuthSuccessAdditionalHandler> _refreshAuthSuccessHandlers;
    protected List<KAuthFailureAdditionalHandler> _refreshAuthFailureHandlers;
    //==============================================================================
    protected List<KAccessSuccessHandler> _accessSuccessHandlers;
    //==============================================================================
    protected List<LogoutHandler> _logoutHandlers;
    //==============================================================================

    // init configurer
    //==============================================================================
    public KAuthenticatingConfigurer(KAuthenticatingContext context) {
        this._context = context;
    }

    //==============================================================================
    // Spring-Security component
    //==============================================================================

    protected RememberMeServices buildRememberServices() {
        Assert.notNull(_context.tokenProcessor(), "The token processor can't be null!");
        if (_rememberServices != null) {
            return _rememberServices;
        }
        return new RememberKClientServices(_context.tokenProcessor());
    }

    public KAuthenticatingConfigurer codeService(KCodeService codeService) {
        if (_codeService != null) {
            throw new IllegalStateException("The codeService has been set/loaded!");
        }
        this._codeService = codeService;
        return this;
    }

    public KCodeService currentCodeService() {
        return _codeService;
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
            throw new IllegalStateException("The accountService should be manual set!");
        }
        return _accountService;
    }

    public KAuthenticatingConfigurer setAccountAuthCheckers(List<KAccountAuthChecker> authCheckers) {
        this._accountAuthCheckers = authCheckers;
        return this;
    }


    public KAuthenticatingConfigurer setLogoutHandlers(List<LogoutHandler> logoutHandlers) {
        this._logoutHandlers = logoutHandlers;
        return this;
    }

    public KAuthenticatingConfigurer setAccessHandlers(List<KAccessSuccessHandler> successHandlers) {
        this._accessSuccessHandlers = successHandlers;
        return this;
    }

    public KAuthenticatingConfigurer setAccountAuthAdditionalHandlers(List<KAuthSuccessAdditionalHandler> successHandlers, List<KAuthFailureAdditionalHandler> failureHandlers) {
        this._accountAuthSuccessHandlers = successHandlers;
        this._accountAuthFailureHandlers = failureHandlers;
        return this;
    }

    public KAuthenticatingConfigurer setRefreshAuthAdditionalHandlers(List<KAuthSuccessAdditionalHandler> successHandlers, List<KAuthFailureAdditionalHandler> failureHandlers) {
        this._refreshAuthSuccessHandlers = successHandlers;
        this._refreshAuthFailureHandlers = failureHandlers;
        return this;
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
        return new AffirmativeBased(
                Arrays.asList(
                        new WebExpressionVoter(),
                        new KUrlAccessDecisionVoter()
                                .supervisorGranted(_context.config().isSupervisorGranted())
                                .anonymousAllowed(_context.config().isAnonymousAllowed())
                )
        );
    }

    protected CorsConfigurationSource buildCorsConfigurationSource() {
        return new KClientCorsConfigurationSource(_context.config().getCors());
    }

    protected KLogoutSuccessHandler buildLogoutSuccessHandler() {
        return new KLogoutSuccessHandler();
    }

    protected List<LogoutHandler> buildDefaultLogoutHandlers() {
        List<LogoutHandler> handlers = new ArrayList<>();
        handlers.add(new KLogoutClearCacheHandler(_context));
        return handlers;
    }

    //==============================================================================
    // filter
    //==============================================================================
    protected List<KFilter> buildFilters() {
        List<KFilter> filters = new ArrayList<>();

        filters.add(new KFilter(buildKLocaleContextRequestFilter(), KFilter.Position.BEFORE, LogoutFilter.class));
        filters.add(new KFilter(buildKLogoutFilter(), KFilter.Position.AT, LogoutFilter.class));
        if (_context.config().getAuthByAccount()) {
            filters.add(new KFilter(buildKAccountAuthFilter(), KFilter.Position.AFTER, AbstractPreAuthenticatedProcessingFilter.class));
        }
        if (_context.config().getAuthByCode()) {
            filters.add(new KFilter(buildKCodeAuthFilter(), KFilter.Position.AFTER, AbstractPreAuthenticatedProcessingFilter.class));
        }
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
        accountAuthFilter.setAdditionalSuccessHandlers(_accountAuthSuccessHandlers);
        accountAuthFilter.setAdditionalFailureHandlers(_accountAuthFailureHandlers);
        return accountAuthFilter;
    }

    private KCodeAuthFilter buildKCodeAuthFilter() {
        KCodeAuthFilter codeAuthFilter = new KCodeAuthFilter();
        codeAuthFilter.setRememberMeServices(buildRememberServices());
        codeAuthFilter.setAuthenticationManager(currentAuthenticationManager());
        codeAuthFilter.setAdditionalSuccessHandlers(_accountAuthSuccessHandlers);
        codeAuthFilter.setAdditionalFailureHandlers(_accountAuthFailureHandlers);
        return codeAuthFilter;
    }

    private KRefreshAuthFilter buildKRefreshAuthFilter() {
        KRefreshAuthFilter refreshAuthFilter = new KRefreshAuthFilter();
        refreshAuthFilter.setRememberMeServices(buildRememberServices());
        refreshAuthFilter.setAuthenticationManager(currentAuthenticationManager());
        refreshAuthFilter.setAdditionalSuccessHandlers(_refreshAuthSuccessHandlers);
        refreshAuthFilter.setAdditionalFailureHandlers(_refreshAuthFailureHandlers);
        return refreshAuthFilter;
    }

    private KLogoutFilter buildKLogoutFilter() {
        List<LogoutHandler> handlers = buildDefaultLogoutHandlers();
        if (_logoutHandlers != null && _logoutHandlers.size() > 0) {
            handlers.addAll(_logoutHandlers);
        }
        return new KLogoutFilter(buildLogoutSuccessHandler(), handlers.toArray(new LogoutHandler[0]));
    }

    private KLocaleContextRequestFilter buildKLocaleContextRequestFilter() {
        return new KLocaleContextRequestFilter(new KLocaleContextResolver(_context._config.getLocale()));
    }

    //==============================================================================
    // provider
    //==============================================================================


    protected List<AuthenticationProvider> buildProviders() {
        List<AuthenticationProvider> providers = new ArrayList<>();
        if (_context.config().getAuthByAccount()) {
            providers.add(buildKAccountAuthProvider());
        }
        if (_context.config().getAuthByCode()) {
            providers.add(buildKCodeAuthProvider());
        }
        providers.add(buildKRefreshAuthProvider());
        providers.add(buildKAccessTokenProvider());

        return providers;
    }

    private KAccountAuthProvider buildKAccountAuthProvider() {
        return new KAccountAuthProvider(currentAccountService())
                .userCache(_context.accountCache())
                .passwordEncoder(_context.passwordEncoder())
                .locker(_context.accountLocker())
                .lockSupervisor(_context.config().getAuth().getLockSupervisor())
                .failureCounter(_context.authCounter())
                .additionalCheckers(this._accountAuthCheckers);
    }

    private KCodeAuthProvider buildKCodeAuthProvider() {
        return new KCodeAuthProvider(currentCodeService());
    }

    private KRefreshAuthProvider buildKRefreshAuthProvider() {
        KRefreshAuthProvider refreshAuthProvider = new KRefreshAuthProvider(currentAccountService(), _context.tokenProcessor());
        refreshAuthProvider.userCache(_context.accountCache());
        return refreshAuthProvider;
    }

    private KAccessTokenProvider buildKAccessTokenProvider() {
        KAccessTokenProvider accessTokenProvider = new KAccessTokenProvider(currentAccountService(), _context.tokenProcessor());
        accessTokenProvider.userCache(_context.accountCache());
        accessTokenProvider.setSuccessHandlers(_accessSuccessHandlers);
        return accessTokenProvider;
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

        if (_context.config().getPublicUrls() != null) {
            http.authorizeRequests()
                    .antMatchers(_context.config().getPublicUrls().toArray(new String[0])).permitAll()
                    .anyRequest().authenticated().accessDecisionManager(buildAccessDecisionManager());
        } else {
            http.authorizeRequests().anyRequest().authenticated().accessDecisionManager(buildAccessDecisionManager());
        }
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
