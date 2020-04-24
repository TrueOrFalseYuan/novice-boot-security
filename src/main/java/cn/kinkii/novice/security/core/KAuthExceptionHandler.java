package cn.kinkii.novice.security.core;

import cn.kinkii.novice.security.web.utils.AuthResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class KAuthExceptionHandler {

    public static void handle(HttpServletRequest request, HttpServletResponse response, Throwable failed) throws IOException {
        AuthResponseUtils.error(response, failed);
    }

}
