package cn.kinkii.novice.security.web.auth;

import lombok.Setter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class KAccountAuthFilter extends KAuthenticatingFilter<KAccountAuthToken> {

    private static final String DEFAULT_USERNAME_PARAMETER = "username";
    private static final String DEFAULT_PASSWORD_PARAMETER = "password";

    @Setter
    private String usernameParameter = DEFAULT_USERNAME_PARAMETER;
    @Setter
    private String passwordParameter = DEFAULT_PASSWORD_PARAMETER;

    @Setter
    private boolean postOnly = true;

    public KAccountAuthFilter() {
        super(new AntPathRequestMatcher("/auth/token"));
    }

    @Override
    protected KAccountAuthToken buildAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            return new KAccountAuthToken(this.obtainUsername(request), this.obtainPassword(request));
        }
    }

    @Override
    protected void preCheckAuthentication(KAccountAuthToken builtAuthentication, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        super.preCheckAuthentication(builtAuthentication, request, response);
        if (!StringUtils.hasText(builtAuthentication.getClientId())) {
            builtAuthentication.getKClientDetails().getClient().setClientId(UUID.randomUUID().toString());
        }
    }

    private String obtainUsername(HttpServletRequest request) {
        String username = request.getParameter(this.usernameParameter);
        if (username == null) {
            username = "";
        }
        return username.trim();
    }

    private String obtainPassword(HttpServletRequest request) {
        String password = request.getParameter(this.passwordParameter);
        if (password == null) {
            password = "";
        }
        return password;
    }

}
