package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.token.exception.MissingRawTokenException;
import cn.kinkii.novice.security.web.KHeader;
import cn.kinkii.novice.security.web.MissingClientDetailsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KRefreshAuthFilter extends KAuthenticatingFilter<KRefreshAuthToken> {
    public KRefreshAuthFilter() {
        super(new AntPathRequestMatcher("/auth/refresh"));
    }

    private static String obtainRawRefreshToken(HttpServletRequest request) {
        return request.getHeader(KHeader.K_HEADER_REFRESH_TOKEN);
    }

    @Override
    protected KRefreshAuthToken buildAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String refreshToken = obtainRawRefreshToken(request);
        if (!StringUtils.hasText(refreshToken)) {
            throw new MissingRawTokenException("The refresh token can't be retrieved from the requested http header!");
        }
        logger.debug("Refreshing user authentication by token - " + refreshToken);

        return new KRefreshAuthToken(refreshToken);
    }

    @Override
    protected void preCheckAuthentication(KRefreshAuthToken builtAuthentication, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        super.preCheckAuthentication(builtAuthentication, request, response);
        if (!StringUtils.hasText(builtAuthentication.getClientId())) {
            throw new MissingClientDetailsException("Please request with the client id!");
        }
    }
}
