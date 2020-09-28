package cn.kinkii.novice.security.core;

import cn.kinkii.novice.security.web.utils.AuthRequestUtils;
import cn.kinkii.novice.security.web.utils.AuthResponseUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class KAuthExceptionHandler {

    public static void handle(HttpServletRequest request, HttpServletResponse response, Throwable failed) throws IOException {
        log.error("Authentication failed on " + request.getRequestURI() + " - " + AuthRequestUtils.parseClientAddress(request));
        AuthResponseUtils.error(response, failed);
    }

}
