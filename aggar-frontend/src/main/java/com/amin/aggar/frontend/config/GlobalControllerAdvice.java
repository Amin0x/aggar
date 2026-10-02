package com.amin.aggar.frontend.config;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Locale;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    public static final String SESSION_USER_KEY = "loggedInUser";

    @Value("${external.api.base-url:http://localhost:8080}")
    private String apiBaseUrl;

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session, HttpServletRequest request) {
        Object user = session.getAttribute(SESSION_USER_KEY);
        Locale locale = RequestContextUtils.getLocale(request);
        model.addAttribute("isAuthenticated", user != null);
        model.addAttribute("currentUser", user);
        model.addAttribute("apiBaseUrl", apiBaseUrl);
        model.addAttribute("currentLanguage", locale.getLanguage());
        model.addAttribute("textDirection", "ar".equals(locale.getLanguage()) ? "rtl" : "ltr");
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String pagePath = requestUri.substring(contextPath.length());
        String languagePath = "/" + locale.getLanguage();
        if (pagePath.equals(languagePath)) {
            pagePath = "/";
        } else if (pagePath.startsWith(languagePath + "/")) {
            pagePath = pagePath.substring(languagePath.length());
        }
        String applicationPath = contextPath.endsWith(languagePath)
                ? contextPath.substring(0, contextPath.length() - languagePath.length())
                : contextPath;
        String targetLanguage = "ar".equals(locale.getLanguage()) ? "en" : "ar";
        String languageSwitchUrl = applicationPath + "/" + targetLanguage + pagePath;
        if (request.getQueryString() != null) {
            languageSwitchUrl += "?" + request.getQueryString();
        }
        model.addAttribute("languageSwitchUrl", languageSwitchUrl);
    }
}
