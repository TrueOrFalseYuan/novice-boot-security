package cn.kinkii.novice.security.web.logout;

import cn.kinkii.novice.security.core.KAuthException;
import cn.kinkii.novice.security.core.KAuthExceptionHandler;
import cn.kinkii.novice.security.i18n.KSecurityMessageUtils;
import cn.kinkii.novice.security.token.exception.MissingRawTokenException;
import cn.kinkii.novice.security.web.KClientDetails;
import cn.kinkii.novice.security.web.KHeader;
import cn.kinkii.novice.security.web.MissingClientDetailsException;
import cn.kinkii.novice.security.web.access.KAccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class KLogoutFilter extends GenericFilterBean {

    private static final RequestMatcher LOGOUT_MATCHER = new AntPathRequestMatcher("/auth/logout");

    private static boolean requestLogout(HttpServletRequest request) {
        return LOGOUT_MATCHER.matches(request);
    }

    private final LogoutHandler handler;
    private final LogoutSuccessHandler successHandler;

    private static String obtainRawAccessToken(HttpServletRequest request) {
        String result = request.getHeader(KHeader.K_HEADER_ACCESS_TOKEN);
        if (!StringUtils.hasText(result)) {
            result = request.getParameter(KHeader.K_HEADER_ACCESS_TOKEN.toLowerCase());
        }
        return result;
    }

    public KLogoutFilter(LogoutSuccessHandler logoutSuccessHandler, LogoutHandler... handlers) {
        Assert.notNull(logoutSuccessHandler, "logoutSuccessHandler cannot be null");
        this.successHandler = logoutSuccessHandler;
        this.handler = new CompositeLogoutHandler(handlers);
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (requestLogout(request)) {
            try {
                KClientDetails clientDetails = new KClientDetails(request);
                if (StringUtils.hasText(clientDetails.getClientId())) {
                    String accessToken = obtainRawAccessToken(request);
                    if (!StringUtils.hasText(accessToken)) {
                        log.debug("The access token can't be retrieved from the requested http header!");
                        throw new MissingRawTokenException(KSecurityMessageUtils.getExceptionMessage(MissingRawTokenException.class));
                    }
                    log.debug("Logout with access token - " + accessToken);

                    KAccessToken kAccessToken = new KAccessToken(accessToken);
                    kAccessToken.setKClientDetails(clientDetails);

                    this.handler.logout(request, response, kAccessToken);
                    this.successHandler.onLogoutSuccess(request, response, kAccessToken);
                } else {
                    throw new MissingClientDetailsException("Please request with the client id!");
                }
            } catch (KAuthException e) {
                KAuthExceptionHandler.handle(request, response, e);
            }
            return;
        }

        chain.doFilter(request, response);
    }
}
