package cn.kinkii.novice.security.token.exception;

public class IllegalRawTokenException extends KRawTokenException {
    public IllegalRawTokenException(String msg, Throwable t) {
        super(msg, t);
    }

    public IllegalRawTokenException(String msg) {
        super(msg);
    }
}
