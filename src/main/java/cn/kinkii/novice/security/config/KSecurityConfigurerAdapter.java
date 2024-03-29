package cn.kinkii.novice.security.config;

import cn.kinkii.novice.security.context.KAuthenticatingConfig;
import cn.kinkii.novice.security.context.KAuthenticatingConfigurer;
import cn.kinkii.novice.security.context.KAuthenticatingContext;
import cn.kinkii.novice.security.model.KAccount;
import cn.kinkii.novice.security.service.KCodeService;
import cn.kinkii.novice.security.web.access.KAccessSuccessHandler;
import cn.kinkii.novice.security.service.KAccountService;
import cn.kinkii.novice.security.web.auth.KAccountAuthChecker;
import cn.kinkii.novice.security.web.auth.KAuthFailureAdditionalHandler;
import cn.kinkii.novice.security.web.auth.KAuthSuccessAdditionalHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.List;

@SuppressWarnings("RedundantThrows")
public abstract class KSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    @Lazy
    protected KAuthenticatingConfigurer kAuthenticatingConfigurer;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        kAuthenticatingConfigurer.configureAuthenticationManagerBuilder(auth);
        additionalConfigure(auth);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        kAuthenticatingConfigurer.configureHttpSecurity(http);
        additionalConfigure(http);
    }

    protected abstract void additionalConfigure(AuthenticationManagerBuilder auth);

    protected abstract void additionalConfigure(HttpSecurity http);

    @Bean
    public AuthenticationManager getManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean(name = "kAuthenticatingConfig")
    @ConfigurationProperties(prefix = KAuthenticatingConfig.CONFIG_PREFIX)
    public KAuthenticatingConfig getConfigBean() {
        return new KAuthenticatingConfig();
    }

    @Bean(name = "kAuthenticatingContext")
    @DependsOn({"kAuthenticatingConfig"})
    public KAuthenticatingContext getContextBean(@Autowired KAuthenticatingConfig kAuthenticatingConfig,
                                                 @Autowired(required = false) RedisConnectionFactory redisConnectionFactory) {
        return new KAuthenticatingContext().preset(redisConnectionFactory).init(kAuthenticatingConfig);
    }

    @Bean(name = "kAuthenticatingConfigurer")
    @DependsOn({"kAuthenticatingContext"})
    public KAuthenticatingConfigurer getConfigurerBean(@Autowired KAuthenticatingContext kAuthenticatingContext,
                                                       @Autowired AuthenticationManager authenticationManager,
                                                       @Autowired KAccountService accountService,
                                                       @Qualifier("accountAuthCheckers") @Autowired(required = false) List<KAccountAuthChecker> accountAuthCheckers,
                                                       @Autowired(required = false) KCodeService codeService,
                                                       @Qualifier("logoutHandlers") @Autowired(required = false) List<LogoutHandler> logoutHandlers,
                                                       @Qualifier("accountSuccessHandlers") @Autowired(required = false) List<KAuthSuccessAdditionalHandler> accountSuccessHandlers,
                                                       @Qualifier("accountFailureHandlers") @Autowired(required = false) List<KAuthFailureAdditionalHandler> accountFailureHandlers,
                                                       @Qualifier("refreshSuccessHandlers") @Autowired(required = false) List<KAuthSuccessAdditionalHandler> refreshSuccessHandlers,
                                                       @Qualifier("refreshFailureHandlers") @Autowired(required = false) List<KAuthFailureAdditionalHandler> refreshFailureHandlers,
                                                       @Autowired(required = false) List<KAccessSuccessHandler> accessSuccessHandlers) {
        return new KAuthenticatingConfigurer(kAuthenticatingContext)
                .accountService(accountService)
                .setAccountAuthCheckers(accountAuthCheckers)
                .codeService(codeService)
                .setAccountAuthAdditionalHandlers(accountSuccessHandlers, accountFailureHandlers)
                .setRefreshAuthAdditionalHandlers(refreshSuccessHandlers, refreshFailureHandlers)
                .setAccessHandlers(accessSuccessHandlers)
                .setLogoutHandlers(logoutHandlers)
                .authenticationManager(authenticationManager);
    }

}

