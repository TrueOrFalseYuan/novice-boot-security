package cn.kinkii.novice.security.web.auth;

import lombok.Setter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class KCodeAuthFilter extends KAuthenticatingFilter<KCodeAuthToken> {

    private static final String DEFAULT_CODE_PARAMETER = "code";

    @Setter
    private String codeParameter = DEFAULT_CODE_PARAMETER;

    @Setter
    private boolean postOnly = true;

    public KCodeAuthFilter() {
        super(new AntPathRequestMatcher("/auth/token/by-code"));
    }

    @Override
    protected KCodeAuthToken buildAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String code = this.obtainCode(request);
            if (StringUtils.hasText(code)) {
                KCodeAuthToken codeAuthToken = new KCodeAuthToken(code);
                codeAuthToken.setAdditionalParams(this.obtainAdditionalParams(request));
                return codeAuthToken;
            }
            throw new AuthenticationServiceException("Failed to get the authentication code!");
        }
    }

    @Override
    protected void preCheckAuthentication(KCodeAuthToken builtAuthentication, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        super.preCheckAuthentication(builtAuthentication, request, response);
        if (!StringUtils.hasText(builtAuthentication.getClientId())) {
            builtAuthentication.getKClientDetails().getClient().setClientId(UUID.randomUUID().toString());
        }
    }

    private String obtainCode(HttpServletRequest request) {
        String code = request.getParameter(this.codeParameter);
        if (code == null) {
            code = "";
        }
        return code.trim();
    }

    private Map<String, String[]> obtainAdditionalParams(HttpServletRequest request) {
        Map<String, String[]> additionalParams = new HashMap<>();
        for (String key : request.getParameterMap().keySet()) {
            if (!this.codeParameter.equals(key)) {
                additionalParams.put(key, request.getParameterMap().get(key));
            }
        }
        return additionalParams;
    }

}
