package cn.kinkii.novice.security.web.locale;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Getter
@Setter
public class KLocaleConfig {

    private String defaultLocale;

    private String localeParam;

    public Locale getDefaultLocale() {
        Locale locale = Locale.getDefault();
        if (StringUtils.hasText(defaultLocale)) {
            locale = StringUtils.parseLocale(defaultLocale);
        }
        return locale;
    }

}
