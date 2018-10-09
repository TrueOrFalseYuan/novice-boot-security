package cn.kinkii.novice.security.web.locker;

import cn.kinkii.novice.security.core.KAuthException;

public class KAccountLockedException extends KAuthException {
    public KAccountLockedException(String msg, Throwable t) {
        super(msg, t);
    }

    public KAccountLockedException(String msg) {
        super(msg);
    }
}