package com.amin.aggar.frontend.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Map;

@Component
public class ApiBearerTokenInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            org.springframework.http.HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        URI apiBaseUri = URI.create(apiBaseUrl);
        URI requestUri = request.getURI();
        boolean sameApi = apiBaseUri.getScheme().equals(requestUri.getScheme())
                && apiBaseUri.getHost().equals(requestUri.getHost())
                && apiBaseUri.getPort() == requestUri.getPort();
        String path = requestUri.getPath();
        String basePath = apiBaseUri.getPath() == null ? "" : apiBaseUri.getPath();
        boolean publicAuthenticationEndpoint = request.getMethod() == HttpMethod.POST
                && (path.equals(basePath + "/users/authenticate")
                || path.equals(basePath + "/users"));
        ServletRequestAttributes attributes = RequestContextHolder.getRequestAttributes()
                instanceof ServletRequestAttributes servletAttributes ? servletAttributes : null;
        if (sameApi && !publicAuthenticationEndpoint && attributes != null) {
            HttpSession session = attributes.getRequest().getSession(false);
            if (session != null
                    && session.getAttribute(GlobalControllerAdvice.SESSION_USER_KEY) instanceof Map<?, ?> user
                    && user.get("accessToken") instanceof String token
                    && user.get("expiresAt") instanceof String expiresAt
                    && Instant.parse(expiresAt).isAfter(Instant.now())
                    && !token.isBlank()) {
                request.getHeaders().setBearerAuth(token);
            }
        }
        ClientHttpResponse response = execution.execute(request, body);
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED
                && request.getMethod() == HttpMethod.GET
                && isPublicPropertyDetail(path, basePath)
                && hasInvalidBearerToken(response)) {
            response.close();
            if (attributes != null) {
                HttpSession session = attributes.getRequest().getSession(false);
                if (session != null) {
                    session.removeAttribute(GlobalControllerAdvice.SESSION_USER_KEY);
                }
            }
            request.getHeaders().remove(HttpHeaders.AUTHORIZATION);
            return execution.execute(request, body);
        }
        return response;
    }

    private boolean isPublicPropertyDetail(String path, String basePath) {
        String propertyPath = basePath + "/properties/";
        if (!path.startsWith(propertyPath)) {
            return false;
        }
        String identifier = path.substring(propertyPath.length());
        return identifier.matches("\\d+") || identifier.matches("view/[^/]+");
    }

    private boolean hasInvalidBearerToken(ClientHttpResponse response) throws IOException {
        return response.getHeaders().getOrEmpty(HttpHeaders.WWW_AUTHENTICATE).stream()
                .anyMatch(header -> header.contains("error=\"invalid_token\""));
    }

    private final String apiBaseUrl;

    public ApiBearerTokenInterceptor(
            @org.springframework.beans.factory.annotation.Value("${external.api.base-url:http://localhost:8080}")
            String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }
}
