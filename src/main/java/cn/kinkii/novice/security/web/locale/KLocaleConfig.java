package cn.kinkii.novice.security.web.locale;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Getter
@Setter
public class KLocaleConfig {

    public static final String LOCALE_SPLITTER = "_";

    private String defaultLocale;

    private String localeParam;

    public Locale getDefaultLocale() {
        Locale locale = Locale.getDefault();
        if (StringUtils.hasText(defaultLocale)) {
            String[] locales = defaultLocale.split(LOCALE_SPLITTER);
            if (locales.length > 1) {
                locale = new Locale(locales[0], locales[1]);
            } else {
                locale = new Locale(locales[0]);
            }
        }
        return locale;
    }

}
