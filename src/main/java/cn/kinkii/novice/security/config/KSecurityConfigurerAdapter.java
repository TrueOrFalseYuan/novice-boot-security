package cn.kinkii.novice.security.config;

import cn.kinkii.novice.security.context.KAuthenticatingConfig;
import cn.kinkii.novice.security.context.KAuthenticatingConfigurer;
import cn.kinkii.novice.security.service.KAccountService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SuppressWarnings("RedundantThrows")
public abstract class KSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter implements InitializingBean {

    @Autowired
    protected KAuthenticatingConfigurer securityConfigurer;

    @Autowired
    protected KAccountService accountService;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        securityConfigurer.configureAuthenticationManagerBuilder(auth);
        additionalConfigure(auth);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        securityConfigurer.configureHttpSecurity(http);
        additionalConfigure(http);
    }

    protected abstract void additionalConfigure(AuthenticationManagerBuilder auth);

    protected abstract void additionalConfigure(HttpSecurity http);

    @Bean
    public AuthenticationManager getManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @ConfigurationProperties(prefix = KAuthenticatingConfig.CONFIG_PREFIX)
    public KAuthenticatingConfig getConfigBean() {
        return new KAuthenticatingConfig();
    }

    @Bean
    public KAuthenticatingConfigurer getConfigurerBean(@Autowired KAuthenticatingConfig securityConfig) {
        return new KAuthenticatingConfigurer(securityConfig);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        securityConfigurer.accountService(accountService).authenticationManager(authenticationManager);
    }
}

