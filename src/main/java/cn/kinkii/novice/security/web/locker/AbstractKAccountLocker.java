package cn.kinkii.novice.security.web.locker;

import lombok.Getter;

public abstract class AbstractKAccountLocker implements KAccountLocker {

    protected static final int DEFAULT_LOCK_SECONDS = 3600;

    @Getter
    protected int lockSeconds;

    public AbstractKAccountLocker() {
        this(DEFAULT_LOCK_SECONDS);
    }

    public AbstractKAccountLocker(int lockSeconds) {
        this.lockSeconds = lockSeconds;
    }
}
