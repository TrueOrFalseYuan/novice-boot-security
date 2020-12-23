package cn.kinkii.novice.security.core;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

public class KAuthException extends AuthenticationException {

    @Getter
    private Object detail;

    public KAuthException(String msg, Object detail, Throwable t) {
        super(msg, t);
        this.detail = detail;
    }

    public KAuthException(String msg, Throwable t) {
        super(msg, t);
    }

    public KAuthException(String msg) {
        super(msg);
    }

}
