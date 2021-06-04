package cn.kinkii.novice.security.web.locale;

import java.util.Locale;

public class DefaultKLocaleConfig extends KLocaleConfig {

    private static final String DEFAULT_LOCALE_PARAM = "_locale";

    public DefaultKLocaleConfig() {
        setDefaultLocale(Locale.getDefault().toString());
        setLocaleParam(DEFAULT_LOCALE_PARAM);
    }

}
