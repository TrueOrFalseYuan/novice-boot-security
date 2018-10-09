package cn.kinkii.novice.security.service;

import cn.kinkii.novice.security.core.KAuthException;

public class KAccountNotFoundException extends KAuthException {
    public KAccountNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public KAccountNotFoundException(String msg) {
        super(msg);
    }
}
