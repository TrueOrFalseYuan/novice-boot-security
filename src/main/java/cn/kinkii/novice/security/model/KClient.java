package cn.kinkii.novice.security.model;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class KClient {
    @NonNull
    private String clientId;
    private String clientType;
    private String clientVersion;
    private String clientAddress;
}