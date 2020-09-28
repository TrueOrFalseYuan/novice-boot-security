package cn.kinkii.novice.security.web;

import cn.kinkii.novice.security.model.KClient;
import cn.kinkii.novice.security.web.utils.AuthRequestUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class KClientDetails implements Serializable {

    private static final long serialVersionUID = -7741959334900422968L;

    private final KClient client;

    public KClientDetails(HttpServletRequest request) {
        this.client = obtainClient(request);
    }

    private static KClient obtainClient(HttpServletRequest request) {
        String clientHeader = request.getHeader(KHeader.K_HEADER_CLIENT);
        if (!StringUtils.hasText(clientHeader)) {
            clientHeader = request.getParameter(KHeader.K_HEADER_CLIENT.toLowerCase());
        }
        if (!StringUtils.hasText(clientHeader)) {
            return new KClient();
        }
        String clientType = null;
        String clientVersion = null;
        String clientId = null;
        String[] clientInfoGroup = clientHeader.split(";");
        for (String info : clientInfoGroup) {
            String[] pair = info.split(":");
            if (pair.length == 2) {
                String key = pair[0].trim();
                String value = pair[1].trim();
                if (key.length() == 0 || value.length() == 0) {
                    continue;
                }
                if (KHeader.K_HEADER_CLIENT_PARAM_NAME.equalsIgnoreCase(key)) {
                    clientType = value;
                } else if (KHeader.K_HEADER_CLIENT_PARAM_VERSION.equalsIgnoreCase(key)) {
                    clientVersion = value;
                } else if (KHeader.K_HEADER_CLIENT_PARAM_ID.equalsIgnoreCase(key)) {
                    clientId = value;
                }
            }
        }

        return new KClient(clientId, clientType, clientVersion, AuthRequestUtils.parseClientAddress(request));
    }

    public String getClientId() {
        if (client != null) {
            return client.getClientId();
        }
        return null;
    }
}
