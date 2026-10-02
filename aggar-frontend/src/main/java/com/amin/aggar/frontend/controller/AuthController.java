package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.config.GlobalControllerAdvice;
import com.amin.aggar.frontend.dto.PropertyDto;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;
    private final MessageSource messageSource;

    public AuthController(RestTemplate restTemplate,
                          @Value("${external.api.url:http://localhost:8080/api}") String apiBaseUrl,
                          MessageSource messageSource) {
        this.restTemplate = restTemplate;
        this.apiBaseUrl = apiBaseUrl;
        this.messageSource = messageSource;
    }

    @GetMapping("/login")
    public String loginPage(Model model,
                           @RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "redirect", required = false) String redirect,
                           @RequestParam(value = "registered", required = false) String registered,
                           HttpSession session,
                           Locale locale) {

        if (session.getAttribute(GlobalControllerAdvice.SESSION_USER_KEY) != null) {
            return "redirect:/home";
        }

        if (error != null) {
            model.addAttribute("errorMessage", messageSource.getMessage("login.error.invalid", null, locale));
        }

        if (logout != null) {
            model.addAttribute("message", messageSource.getMessage("login.success.logout", null, locale));
        }

        if (registered != null) {
            model.addAttribute("message", messageSource.getMessage("register.success", null, locale));
        }

        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        model.addAttribute("redirect", redirect);

        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("identifier") String identifier,
                       @RequestParam("password") String password,
                       @RequestParam(value = "redirect", required = false) String redirect,
                       HttpSession session,
                       Model model) {

        try {
            String url = UriComponentsBuilder.fromUriString(apiBaseUrl)
                    .pathSegment("users", "authenticate")
                    .toUriString();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(Map.of("identifier", identifier, "password", password), headers),
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> user = response.getBody();
            if (user != null) {
                session.setAttribute(GlobalControllerAdvice.SESSION_USER_KEY, user);
                log.info("User {} logged in successfully", identifier);
                if (redirect != null && !redirect.isEmpty()) {
                    return "redirect:" + redirect;
                }
                return "redirect:/home";
            }

            log.warn("Failed login attempt for identifier: {}", identifier);
            return "redirect:/login?error";

        } catch (HttpClientErrorException.Unauthorized ex) {
            log.warn("Failed login attempt for identifier: {}", identifier);
            return "redirect:/login?error";
        } catch (Exception ex) {
            log.error("Login error for identifier: {}", identifier, ex);
            return "redirect:/login?error";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        log.info("User logged out");
        return "redirect:/login?logout";
    }

    @GetMapping("/register")
    public String registerPage(Model model,
                               @RequestParam(value = "error", required = false) String error,
                               Locale locale) {

        if (error != null) {
            model.addAttribute("errorMessage", messageSource.getMessage("register.error.failed", null, locale));
        }

        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());

        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                          @RequestParam("email") String email,
                          @RequestParam("password") String password,
                          @RequestParam("confirmPassword") String confirmPassword,
                          @RequestParam("name") String name,
                          @RequestParam("phone") String phone,
                          Model model,
                          Locale locale) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", messageSource.getMessage("register.error.password-mismatch", null, locale));
            return "register";
        }

        if (password.length() < 8) {
            model.addAttribute("errorMessage", messageSource.getMessage("register.error.password-length", null, locale));
            return "register";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> userDto = Map.of(
                    "username", username,
                    "password", password,
                    "name", name,
                    "email", email,
                    "phone", phone,
                    "role", "owner"
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(userDto, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiBaseUrl + "/users",
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("New user registered: {} ({})", username, email);
                return "redirect:/login?registered";
            } else {
                return "redirect:/register?error";
            }

        } catch (Exception ex) {
            log.error("Registration failed for username: {}", username, ex);
            return "redirect:/register?error";
        }
    }

    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        Object user = session.getAttribute(GlobalControllerAdvice.SESSION_USER_KEY);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());

        return "profile";
    }
}
