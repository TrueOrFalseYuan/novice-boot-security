package cn.kinkii.novice.security.web.auth;

import cn.kinkii.novice.security.web.KClientContainer;
import cn.kinkii.novice.security.web.KClientDetails;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public class KCodeAuthToken extends AbstractAuthenticationToken implements KClientContainer {

    // ~ Instance fields
    // ================================================================================================

    @Getter
    @Setter
    private KClientDetails kClientDetails;

    private final String code;

    @Setter
    @Getter
    private Map<String, String[]> additionalParams;

    // ~ Constructors
    // ===================================================================================================

    public KCodeAuthToken(String code) {
        super(null);
        this.code = code;
        setAuthenticated(false);
    }

    public KCodeAuthToken(String code, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.code = code;
        super.setAuthenticated(true); // must use super, as we override
    }

    // ~ Methods
    // ========================================================================================================

    @Override
    public Object getCredentials() {
        return this.code;
    }

    @Override
    public Object getPrincipal() {
        return this.code;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

}
