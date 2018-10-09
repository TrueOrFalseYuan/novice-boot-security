package cn.kinkii.novice.security.web;

import cn.kinkii.novice.security.core.KAuthException;

public class MissingClientDetailsException extends KAuthException {

    public MissingClientDetailsException(String msg, Throwable t) {
        super(msg, t);
    }

    public MissingClientDetailsException(String msg) {
        super(msg);
    }

}