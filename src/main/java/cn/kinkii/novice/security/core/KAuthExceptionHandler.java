package cn.kinkii.novice.security.core;

import cn.kinkii.novice.security.i18n.KSecurityMessageUtils;
import cn.kinkii.novice.security.web.response.KAuthFailureResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class KAuthExceptionHandler {

    private static final String AUTH_FAILURE_DETAIL = "detail";

    private static ObjectMapper mapper = new ObjectMapper();

    private static String toJson(Object object) {
        String json = null;
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException ignored) {
        }
        return json;
    }

    public static void handle(HttpServletRequest request, HttpServletResponse response, Throwable failed) throws IOException, ServletException {
        if(response.isCommitted()) {
            return;
        }
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        OutputStream out = response.getOutputStream();
        out.write(toJson(buildAuthResponse(failed)).getBytes());
        out.flush();
        out.close();
    }

    private static KAuthFailureResponse buildAuthResponse(Throwable failed) {
        Throwable cause = failed;
        if (failed instanceof InternalAuthenticationServiceException && failed.getCause() != null) {
            cause = failed.getCause();
        }
        return KAuthFailureResponse.build(KSecurityMessageUtils.getResponseCode(cause), KSecurityMessageUtils.getResponseMessage(cause)).addValue(AUTH_FAILURE_DETAIL, cause.getLocalizedMessage());
    }

}
