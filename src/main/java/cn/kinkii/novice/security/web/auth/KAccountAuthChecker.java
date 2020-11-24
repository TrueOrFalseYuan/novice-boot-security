package cn.kinkii.novice.security.web.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public interface KAccountAuthChecker {

    void check(UserDetails userDetails, KAccountAuthToken authentication) throws AuthenticationException;

}
