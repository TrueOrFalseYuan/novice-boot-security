package cn.kinkii.novice.security.i18n;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

public class KSecurityMessageSource extends ResourceBundleMessageSource {

    private static MessageSourceAccessor accessor = new MessageSourceAccessor(new KSecurityMessageSource());

    public KSecurityMessageSource() {
        this.setBasenames("cn.kinkii.novice.security.i18n.messages", "cn.kinkii.novice.security.i18n.responses");
    }

    public static MessageSourceAccessor getAccessor() {
        return accessor;
    }

}
