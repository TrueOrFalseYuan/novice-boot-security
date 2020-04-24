package cn.kinkii.novice.security.web.utils;

import cn.kinkii.novice.security.web.KHeader;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class TokenRequestUtils {

    public static String obtainRawToken(HttpServletRequest request, String tokenType) {
        if (KHeader.K_HEADER_ACCESS_TOKEN.equals(tokenType) || KHeader.K_HEADER_REFRESH_TOKEN.equals(tokenType)) {
            String result = request.getHeader(tokenType);
            if (!StringUtils.hasText(result)) {
                result = request.getParameter(tokenType.toLowerCase());
            }
            return result;
        } else {
            throw new IllegalArgumentException("Unsupported token type! - " + tokenType);
        }
    }

    public static String obtainRawAccessToken(HttpServletRequest request) {
        return obtainRawToken(request, KHeader.K_HEADER_ACCESS_TOKEN);
    }

    public static String obtainRawRefreshToken(HttpServletRequest request) {
        return obtainRawToken(request, KHeader.K_HEADER_REFRESH_TOKEN);
    }
}
