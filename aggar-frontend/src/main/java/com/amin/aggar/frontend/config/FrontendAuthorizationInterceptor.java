package com.amin.aggar.frontend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

@Component
public class FrontendAuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String path = request.getServletPath();
        boolean adminRoute = path.startsWith("/admin/")
                || path.equals("/admin")
                || path.startsWith("/states/")
                || path.startsWith("/cities/")
                || path.startsWith("/neighborhoods/");
        boolean authenticatedRoute = adminRoute
                || path.equals("/properties/add")
                || path.startsWith("/properties/add/")
                || path.equals("/properties/cities")
                || path.matches("/properties/[^/]+/(comments|messages)");

        if (!authenticatedRoute) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Object sessionUser = session == null
                ? null
                : session.getAttribute(GlobalControllerAdvice.SESSION_USER_KEY);
        if (!(sessionUser instanceof Map<?, ?> user)
                || !(user.get("accessToken") instanceof String token)
                || token.isBlank()
                || !(user.get("expiresAt") instanceof String expiresAt)
                || !Instant.parse(expiresAt).isAfter(Instant.now())) {
            if (session != null) {
                session.removeAttribute(GlobalControllerAdvice.SESSION_USER_KEY);
            }
            String redirectPath = path;
            if (request.getQueryString() != null) {
                redirectPath += "?" + request.getQueryString();
            }
            String loginPath = request.getContextPath() + "/login?redirect="
                    + URLEncoder.encode(redirectPath, StandardCharsets.UTF_8);
            response.sendRedirect(loginPath);
            return false;
        }

        if (adminRoute && !(user.get("role") instanceof String role
                && role.toLowerCase(Locale.ROOT).equals("admin"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}
