package cn.kinkii.novice.security.token.exception;

import cn.kinkii.novice.security.core.KAuthException;

public abstract class KRawTokenException extends KAuthException {
    public KRawTokenException(String msg, Throwable t) {
        super(msg, t);
    }

    public KRawTokenException(String msg) {
        super(msg);
    }
}
