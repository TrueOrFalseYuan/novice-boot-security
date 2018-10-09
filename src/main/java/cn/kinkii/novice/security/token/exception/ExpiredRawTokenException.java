package cn.kinkii.novice.security.token.exception;

public class ExpiredRawTokenException extends KRawTokenException {
    public ExpiredRawTokenException(String msg, Throwable t) {
        super(msg, t);
    }

    public ExpiredRawTokenException(String msg) {
        super(msg);
    }
}

