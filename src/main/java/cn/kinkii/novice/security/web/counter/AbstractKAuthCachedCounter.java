package cn.kinkii.novice.security.web.counter;

import cn.kinkii.novice.security.i18n.KSecurityMessageUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
public abstract class AbstractKAuthCachedCounter implements KAuthCounter {

    protected static final int DEFAULT_COUNT_LIMIT = 10;
    protected static final int DEFAULT_COUNT_SECONDS = 300;

    @Getter
    protected int countLimit;
    @Getter
    protected int countSeconds;

    protected String lockIgnoreAccount;

    public AbstractKAuthCachedCounter() {
        this(DEFAULT_COUNT_LIMIT, DEFAULT_COUNT_SECONDS,null);
    }

    public AbstractKAuthCachedCounter(int countLimit, int countSeconds,String lockIgnoreAccount) {
        Assert.isTrue(countSeconds > 0, "Please set the proper value for the count period! - current value: " + countSeconds);

        this.countLimit = countLimit;
        this.countSeconds = countSeconds;
        this.lockIgnoreAccount = lockIgnoreAccount;
    }

    @Override
    public void count(String countKey) throws KAuthLimitExceededException {
        Assert.notNull(countKey, "The countKey can't bu null!");
        if (this.countLimit < 0) {
            return;
        }
        if (lockIgnoreAccount.indexOf(countKey) != -1) {
            return;
        }
        Long currentValue = get(countKey);
        if (currentValue >= this.countLimit) {
            log.debug(String.format("The count value of <%s>(%d) has exceeded the auth limit times(%d)!", countKey, currentValue, countLimit));
            throw new KAuthLimitExceededException(KSecurityMessageUtils.getExceptionMessage(KAuthLimitExceededException.class));
        }
        setValue(countKey, ++currentValue);
    }


    @Override
    public Long get(String countKey) {
        Assert.notNull(countKey, "The countKey can't bu null!");
        Long currentValue = getValue(countKey);
        if (currentValue == null) {
            currentValue = 0L;
        }
        return currentValue;
    }


    protected abstract void setValue(String countKey, Long countValue);

    protected abstract Long getValue(String countKey);

}
