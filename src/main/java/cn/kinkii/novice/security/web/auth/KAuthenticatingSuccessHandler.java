package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.web.response.KClientResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class KAuthenticatingSuccessHandler implements AuthenticationSuccessHandler {

    private static ObjectMapper globalObjectMapper = new ObjectMapper();

    static {
        globalObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private List<KAuthSuccessAdditionalHandler> additionalHandlers = new ArrayList<>();

    public void setAdditionalHandlers(List<KAuthSuccessAdditionalHandler> additionalHandlers) {
        this.additionalHandlers = additionalHandlers;
    }

    public KAuthenticatingSuccessHandler addAdditionalHandler(KAuthSuccessAdditionalHandler handler) {
        if (additionalHandlers == null) {
            additionalHandlers = new ArrayList<>();
        }
        additionalHandlers.add(handler);
        return this;
    }

    // copy from SimpleUrlAuthenticationSuccessHandler
    private static void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (!(authentication instanceof KAuthenticatingSuccessToken)) {
            log.info("Please use the KAuthenticatingSuccessHandler with KAuthenticatingToken!");
            return;
        }
        KClientResponse responseToken = ((KAuthenticatingSuccessToken) authentication).getKClientResponse();
        Assert.notNull(responseToken, "Please init the KClientResponse to response on successful authentication!");
        if (additionalHandlers != null) {
            additionalHandlers.forEach(handler -> handler.handle(request, response, authentication));
        }
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        globalObjectMapper.writeValue(response.getWriter(), responseToken);

        clearAuthenticationAttributes(request);
    }

}

