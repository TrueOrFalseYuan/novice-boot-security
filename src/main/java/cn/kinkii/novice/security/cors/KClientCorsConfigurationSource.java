package cn.kinkii.novice.security.cors;

import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KClientCorsConfigurationSource implements CorsConfigurationSource {

    private static final List<String> DEFAULT_ALLOWED_METHODS;
    private static final List<String> DEFAULT_ALLOWED_HEADERS = Collections.unmodifiableList(Collections.singletonList(CorsConfiguration.ALL));
    private static final Long DEFAULT_MAX_AGE = 3600L;

    static {
        List<String> methods = new ArrayList<>();
        Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.PATCH).forEach(method -> methods.add(method.name()));
        DEFAULT_ALLOWED_METHODS = Collections.unmodifiableList(methods);
    }

    private final KClientCorsConfig corsConfig;

    public KClientCorsConfigurationSource(KClientCorsConfig corsConfig) {
        Assert.notNull(corsConfig, "The cors config can't bu null!");
        Assert.notEmpty(corsConfig.getAllowedOrigins(), "Please config the allowed origins or use the DefaultKClientCorsConfig.");
        this.corsConfig = corsConfig;
    }

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest httpServletRequest) {
        CorsConfiguration cc = new CorsConfiguration();
        cc.setAllowedHeaders(DEFAULT_ALLOWED_HEADERS);
        cc.setAllowedMethods(DEFAULT_ALLOWED_METHODS);
        cc.setAllowedOriginPatterns(corsConfig.getAllowedOrigins());
        cc.setAllowCredentials(true);
        cc.setMaxAge(DEFAULT_MAX_AGE);
        return cc;
    }
}
