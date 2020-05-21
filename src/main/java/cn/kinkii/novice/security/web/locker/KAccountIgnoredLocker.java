package cn.kinkii.novice.security.web.locker;

public class KAccountIgnoredLocker implements KAccountLocker {

    @Override
    public void lock(String username, Long lockTime) {
    }

    @Override
    public void unlock(String username) {
    }

    @Override
    public Boolean isLocked(String username) {
        return false;
    }

}
