package cn.kinkii.novice.security.i18n;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.AuthenticationException;

@Slf4j
public class KSecurityMessageUtils {

    private static final MessageSourceAccessor messages = KSecurityMessageSource.getAccessor();

    private static final String EXCEPTION_MESSAGE = "k.security.exception.message";

    private static final String SUCCESS_RESPONSE_CODE = "k.security.success.response.code";
    private static final String SUCCESS_RESPONSE_MESSAGE = "k.security.success.response.message";

    private static final String ERROR_RESPONSE_CODE = "k.security.failure.response.code";
    private static final String ERROR_RESPONSE_MESSAGE = "k.security.failure.response.message";


    public static String getExceptionMessage(Class<?> failedClass) {
        return getExceptionMessage(failedClass, messages.getMessage(EXCEPTION_MESSAGE));
    }

    public static String getExceptionMessage(Class<?> failedClass, String defaultMessage) {
        String message = defaultMessage;
        String messageKey = EXCEPTION_MESSAGE + "." + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, failedClass.getSimpleName());
        try {
            message = messages.getMessage(messageKey);
        } catch (NoSuchMessageException e) {
            log.debug("Failed to get message for " + messageKey + ", use the default message " + defaultMessage + "!");
        }
        return message;
    }

    public static String getSuccessMessage() {
        return messages.getMessage(SUCCESS_RESPONSE_MESSAGE);
    }

    public static Integer getSuccessCode() {
        return Integer.parseInt(messages.getMessage(SUCCESS_RESPONSE_CODE));
    }

    public static String getErrorMessage(Throwable failed) {
        return getValueByClass(ERROR_RESPONSE_MESSAGE, failed);
    }

    public static Integer getErrorCode(Throwable failed) {
        return Integer.parseInt(getValueByClass(ERROR_RESPONSE_CODE, failed));
    }

    private static String getValueByClass(String prefix, Throwable failed) {
        String value = null;
        String valueKey;
        if (failed.getClass().isAssignableFrom(AuthenticationException.class)) {
            Class<?> currentClass = failed.getClass();
            while (currentClass != AuthenticationException.class && value == null) {
                valueKey = prefix + "." + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, currentClass.getSimpleName());
                try {
                    value = messages.getMessage(valueKey);
                } catch (NoSuchMessageException e) {
                    currentClass = currentClass.getSuperclass();
                }
            }
            if (value == null) {
                value = messages.getMessage(prefix);
            }
        } else {
            valueKey = prefix + "." + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, failed.getClass().getSimpleName());
            try {
                value = messages.getMessage(valueKey);
            } catch (NoSuchMessageException e) {
                value = messages.getMessage(prefix);
            }
        }
        log.info("current value:" + value);
        return value;
    }

}
