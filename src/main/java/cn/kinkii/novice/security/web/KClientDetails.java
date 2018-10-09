package cn.kinkii.novice.security.web;

import cn.kinkii.novice.security.model.KClient;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class KClientDetails implements Serializable {

    private static final long serialVersionUID = -7741959334900422968L;

    private static final List<String> PROXY_HEADER = Arrays.asList("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP");

    private KClient client;

    public KClientDetails(HttpServletRequest request) {
        this.client = obtainClient(request);
    }

    private static String parseClientAddress(HttpServletRequest request) {
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

    private static KClient obtainClient(HttpServletRequest request) {
        KClient result = new KClient();
        String clientHeader = request.getHeader(KHeader.K_HEADER_CLIENT);
        if (!StringUtils.hasText(clientHeader)) {
            clientHeader = request.getParameter(KHeader.K_HEADER_CLIENT.toLowerCase());
        }
        if (!StringUtils.hasText(clientHeader)) {
            return result;
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
        result.setClientId(clientId);
        result.setClientType(clientType);
        result.setClientVersion(clientVersion);
        result.setClientAddress(parseClientAddress(request));

        return result;
    }

    public String getClientId() {
        if (client != null) {
            return client.getClientId();
        }
        return null;
    }
}
