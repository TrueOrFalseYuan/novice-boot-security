package cn.kinkii.novice.security.web.counter;

import cn.kinkii.novice.security.core.KAuthException;

public class KAuthLimitExceededException extends KAuthException {
    public KAuthLimitExceededException(String msg, Throwable t) {
        super(msg, t);
    }

    public KAuthLimitExceededException(String msg) {
        super(msg);
    }
}
