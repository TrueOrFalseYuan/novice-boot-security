package cn.kinkii.novice.security.web.utils;

import cn.kinkii.novice.security.core.KAuthException;
import cn.kinkii.novice.security.i18n.KSecurityMessageUtils;
import cn.kinkii.novice.security.web.response.KAuthResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class AuthResponseUtils {

    private static final String AUTH_FAILURE_DETAIL = "detail";

    private static ObjectMapper globalObjectMapper = new ObjectMapper();

    static {
        globalObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private static String toJson(Object object) {
        String json = null;
        try {
            json = globalObjectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ignored) {
        }
        return json;
    }

    public static void error(HttpServletResponse response, Throwable failed) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        OutputStream out = response.getOutputStream();
        out.write(toJson(buildErrorResponse(failed)).getBytes());
        out.flush();
        out.close();
    }

    public static void success(HttpServletResponse response) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        OutputStream out = response.getOutputStream();
        out.write(toJson(buildSuccessResponse()).getBytes());
        out.flush();
        out.close();
    }

    private static KAuthResponse buildSuccessResponse() {
        return KAuthResponse.build(KSecurityMessageUtils.getSuccessCode(), KSecurityMessageUtils.getSuccessMessage());
    }

    private static KAuthResponse buildErrorResponse(Throwable failed) {
        Throwable cause = failed;
        if (failed instanceof KAuthException && ((KAuthException) cause).getDetail() != null) {
            return KAuthResponse.build(KSecurityMessageUtils.getErrorCode(cause), KSecurityMessageUtils.getErrorMessage(cause)).addValue(AUTH_FAILURE_DETAIL, ((KAuthException) cause).getDetail());
        } else if (failed instanceof InternalAuthenticationServiceException && failed.getCause() != null) {
            cause = failed.getCause();
        }
        return KAuthResponse.build(KSecurityMessageUtils.getErrorCode(cause), KSecurityMessageUtils.getErrorMessage(cause)).addValue(AUTH_FAILURE_DETAIL, cause.getLocalizedMessage());

    }
}
