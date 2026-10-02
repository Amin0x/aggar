package com.amin.aggar.frontend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FrontendLanguagePathFilter extends OncePerRequestFilter {

    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("ar", "en");
    private static final String LANGUAGE_COOKIE = "AGGAR_LANGUAGE";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestUri.substring(contextPath.length());

        String language = languagePrefix(path);
        if (language != null) {
            String languagePath = path.substring(language.length());
            String rewrittenPath = languagePath.isEmpty() ? "/" : languagePath;
            String dispatchPath = rewrittenPath;
            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                private final Locale requestLocale = Locale.forLanguageTag(language.substring(1));

                @Override
                public String getRequestURI() {
                    return contextPath + language + dispatchPath;
                }

                @Override
                public String getServletPath() {
                    return dispatchPath;
                }

                @Override
                public String getContextPath() {
                    return contextPath + language;
                }

                @Override
                public String getHeader(String name) {
                    if ("Accept-Language".equalsIgnoreCase(name)) {
                        return requestLocale.toLanguageTag();
                    }
                    return super.getHeader(name);
                }

                @Override
                public Locale getLocale() {
                    return requestLocale;
                }

                @Override
                public java.util.Enumeration<Locale> getLocales() {
                    return Collections.enumeration(Collections.singleton(requestLocale));
                }
            };
            jakarta.servlet.http.Cookie languageCookie =
                    new jakarta.servlet.http.Cookie(LANGUAGE_COOKIE, language.substring(1));
            languageCookie.setPath("/");
            languageCookie.setHttpOnly(true);
            response.addCookie(languageCookie);
            filterChain.doFilter(wrappedRequest, response);
            return;
        }

        if (path.equals("/")) {
            Locale arabicLocale = Locale.forLanguageTag("ar");
            HttpServletRequest arabicRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if ("Accept-Language".equalsIgnoreCase(name)) {
                        return arabicLocale.toLanguageTag();
                    }
                    return super.getHeader(name);
                }

                @Override
                public Locale getLocale() {
                    return arabicLocale;
                }

                @Override
                public java.util.Enumeration<Locale> getLocales() {
                    return Collections.enumeration(Collections.singleton(arabicLocale));
                }
            };
            filterChain.doFilter(arabicRequest, response);
            return;
        }

        if (path.startsWith("/api/")
                || path.startsWith("/uploads/")
                || path.startsWith("/images/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/webjars/")
                || path.contains(".")) {
            filterChain.doFilter(request, response);
            return;
        }

        String preferredLanguage = request.getCookies() == null ? "ar" : Arrays.stream(request.getCookies())
                .filter(cookie -> LANGUAGE_COOKIE.equals(cookie.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .filter(SUPPORTED_LANGUAGES::contains)
                .findFirst()
                .orElse("ar");
        String location = contextPath + "/" + preferredLanguage + path;
        if (request.getQueryString() != null) {
            location += "?" + request.getQueryString();
        }
        response.setHeader("Location", location);
        response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
    }

    private String languagePrefix(String path) {
        return SUPPORTED_LANGUAGES.stream()
                .filter(language -> path.equals("/" + language) || path.startsWith("/" + language + "/"))
                .findFirst()
                .map(language -> "/" + language)
                .orElse(null);
    }
}
