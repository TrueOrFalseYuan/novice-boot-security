package cn.kinkii.novice.security.web.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthRequestUtils {

    private static final List<String> PROXY_HEADER = Arrays.asList("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP");

    public static String parseClientAddress(HttpServletRequest request) {
        String address = null;
        for (String header : PROXY_HEADER) {
            if (address == null || address.length() == 0 || "unknown".equalsIgnoreCase(address)) {
                address = request.getHeader(header);
            } else {
                break;
            }
        }
        if (address == null || address.length() == 0 || "unknown".equalsIgnoreCase(address)) {
            address = request.getRemoteAddr();
        }
        return address;
    }
    
}
