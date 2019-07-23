package cn.kinkii.novice.security.service;

import cn.kinkii.novice.security.core.KAuthException;

public class KCodeInvalidException extends KAuthException {

    public KCodeInvalidException(String msg, Throwable t) {
        super(msg, t);
    }

    public KCodeInvalidException(String msg) {
        super(msg);
    }

}
