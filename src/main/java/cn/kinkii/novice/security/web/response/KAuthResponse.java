package cn.kinkii.novice.security.web.response;

import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Data(staticConstructor = "build")
public class KAuthResponse {

    @NonNull
    private Integer code;

    @NonNull
    private String message;

    private Map<String, Object> values;

    public KAuthResponse addValue(String key, Object value) {
        if (values == null) {
            values = new HashMap<>();
        }
        values.put(key, value);

        return this;
    }
}
