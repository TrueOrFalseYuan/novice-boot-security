package cn.kinkii.novice.security.token.exception;

public class SingleRawTokenInvalidException extends KRawTokenException{

    public SingleRawTokenInvalidException(String msg, Throwable t) {
        super(msg, t);
    }

    public SingleRawTokenInvalidException(String msg) {
        super(msg);
    }
}
