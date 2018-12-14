package cn.kinkii.novice.security.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class KRawTokenData {
    private final String userId;
    private final String username;
    private final String clientId;
    @Setter
    private String clientType;
    @Setter
    private String clientVersion;
}
