package cn.kinkii.novice.security.core;

import org.springframework.security.core.AuthenticationException;

public class KAuthException extends AuthenticationException {

    public KAuthException(String msg, Throwable t) {
        super(msg, t);
    }

    public KAuthException(String msg) {
        super(msg);
    }

}
