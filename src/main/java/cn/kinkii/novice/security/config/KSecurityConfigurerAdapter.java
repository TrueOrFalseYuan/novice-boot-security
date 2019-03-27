package cn.kinkii.novice.security.config;

import cn.kinkii.novice.security.context.KAuthenticatingConfig;
import cn.kinkii.novice.security.context.KAuthenticatingConfigurer;
import cn.kinkii.novice.security.context.KAuthenticatingContext;
import cn.kinkii.novice.security.service.KAccountService;
import cn.kinkii.novice.security.web.auth.KAuthFailureAdditionalHandler;
import cn.kinkii.novice.security.web.auth.KAuthSuccessAdditionalHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
                                                       @Autowired KAccountService accountService,
                                                       @Autowired AuthenticationManager authenticationManager,
                                                       @Autowired(required = false) List<KAuthFailureAdditionalHandler> failureAdditionalHandlers,
                                                       @Autowired(required = false) List<KAuthSuccessAdditionalHandler> successAdditionalHandlers) {
        return new KAuthenticatingConfigurer(kAuthenticatingContext).accountService(accountService).setAdditionalHandlers(failureAdditionalHandlers, successAdditionalHandlers).authenticationManager(authenticationManager);
    }

}

