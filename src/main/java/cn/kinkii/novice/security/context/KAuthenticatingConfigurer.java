package cn.kinkii.novice.security.context;

import cn.kinkii.novice.security.access.KUrlAccessDecisionVoter;
import cn.kinkii.novice.security.cors.KClientCorsConfigurationSource;
import cn.kinkii.novice.security.rememberme.RememberKClientAuthFilter;
import cn.kinkii.novice.security.rememberme.RememberKClientServices;
import cn.kinkii.novice.security.service.KAccountService;
import cn.kinkii.novice.security.web.KAccessDeniedHandler;
import cn.kinkii.novice.security.web.KAuthenticationEntryPoint;
import cn.kinkii.novice.security.web.access.KAccessTokenProvider;
import cn.kinkii.novice.security.web.auth.*;
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
    protected AuthenticationManager _authenticationManager = null;
    //==============================================================================
    protected RememberMeServices _rememberServices = null;
    //==============================================================================
    protected List<KAuthSuccessAdditionalHandler> _authSuccessHandlers;
    protected List<KAuthFailureAdditionalHandler> _authFailureHandlers;

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

    public KAuthenticatingConfigurer setAuthAdditionalHandlers(List<KAuthSuccessAdditionalHandler> successHandlers, List<KAuthFailureAdditionalHandler> failureHandlers) {
        this._authSuccessHandlers = successHandlers;
        this._authFailureHandlers = failureHandlers;
        return this;
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
        return new AffirmativeBased(Arrays.asList(new WebExpressionVoter(), new KUrlAccessDecisionVoter().supervisorGranted(_context.config().isSupervisorGranted())));
    }

    protected CorsConfigurationSource buildCorsConfigurationSource() {
        return new KClientCorsConfigurationSource(_context.config().getCors());
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
        accountAuthFilter.setAdditionalSuccessHandlers(_authSuccessHandlers);
        accountAuthFilter.setAdditionalFailureHandlers(_authFailureHandlers);
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
        providers.add(new KRefreshAuthProvider(currentAccountService(), _context.tokenProcessor()).userCache(_context.accountCache()));
        providers.add(new KAccessTokenProvider(currentAccountService(), _context.tokenProcessor()).userCache(_context.accountCache()));

        return providers;
    }

    private KAccountAuthProvider buildKAccountAuthProvider() {
        KAccountAuthProvider authProvider = new KAccountAuthProvider(currentAccountService()).userCache(_context.accountCache());
        authProvider.setPasswordEncoder(_context.passwordEncoder());
        authProvider.locker(_context.accountLocker()).failureCounter(_context.authCounter());

        return authProvider;
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
            http.authorizeRequests().antMatchers(_context.config().getPublicUrls().toArray(new String[0])).permitAll();
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
