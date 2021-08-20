package cn.kinkii.novice.security.i18n;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

public class KSecurityMessageSource extends ResourceBundleMessageSource {

    private static final MessageSourceAccessor accessor = new MessageSourceAccessor(new KSecurityMessageSource());

    public KSecurityMessageSource() {
        this.setBasenames("cn.kinkii.novice.security.i18n.messages", "cn.kinkii.novice.security.i18n.responses");
        this.setDefaultEncoding("utf-8");
    }

    public static MessageSourceAccessor getAccessor() {
        return accessor;
    }

}
