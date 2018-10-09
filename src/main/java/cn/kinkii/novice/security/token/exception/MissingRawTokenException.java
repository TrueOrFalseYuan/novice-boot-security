package cn.kinkii.novice.security.token.exception;

public class MissingRawTokenException extends KRawTokenException {
    public MissingRawTokenException(String msg, Throwable t) {
        super(msg, t);
    }

    public MissingRawTokenException(String msg) {
        super(msg);
    }
}
