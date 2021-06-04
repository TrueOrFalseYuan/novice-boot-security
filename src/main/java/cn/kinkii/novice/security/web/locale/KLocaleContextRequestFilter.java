package cn.kinkii.novice.security.web.locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class KLocaleContextRequestFilter extends OncePerRequestFilter {

    private final KLocaleContextResolver localeResolver;

    public KLocaleContextRequestFilter(KLocaleContextResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);

        LocaleContextHolder.setLocaleContext(this.localeResolver.resolveLocaleContext(request));
        try {
            filterChain.doFilter(request, response);
        } finally {
            LocaleContextHolder.resetLocaleContext();
            attributes.requestCompleted();
        }
    }

}