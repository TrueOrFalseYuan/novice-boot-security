package cn.kinkii.novice.security.web.locale;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AbstractLocaleContextResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@SuppressWarnings("NullableProblems")
public class KLocaleContextResolver extends AbstractLocaleContextResolver {

    private final KLocaleConfig localeConfig;

    public KLocaleContextResolver(KLocaleConfig localeConfig) {
        this.localeConfig = localeConfig;
    }

    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        return () -> {
            String localeValue = request.getParameter(localeConfig.getLocaleParam());
            Locale locale = KLocaleContextResolver.this.determineDefaultLocale(request);
            if (StringUtils.hasText(localeValue)) {
                locale = StringUtils.parseLocale(localeValue);
            }
            return locale;
        };
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        //Do nothing...
    }

    private Locale determineDefaultLocale(HttpServletRequest request) {
        Locale defaultLocale = localeConfig.getDefaultLocale();
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
        return defaultLocale;
    }

}
