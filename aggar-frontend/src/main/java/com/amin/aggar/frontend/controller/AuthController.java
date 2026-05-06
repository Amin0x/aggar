package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.config.GlobalControllerAdvice;
import com.amin.aggar.frontend.dto.PropertyDto;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final RestTemplate restTemplate;
    private final String apiUrl;

    public AuthController(RestTemplate restTemplate,
                          @Value("${external.api.properties-url:http://localhost:8080/api}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    @GetMapping("/login")
    public String loginPage(Model model,
                           @RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           HttpSession session) {

        if (session.getAttribute(GlobalControllerAdvice.SESSION_USER_KEY) != null) {
            return "redirect:/home";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }

        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());

        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {

        try {
            String url = UriComponentsBuilder.fromUriString(apiUrl + "/users")
                    .toUriString();

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            if (response.getBody() != null) {
                for (Map<String, Object> user : response.getBody()) {
                    if (username.equals(user.get("username"))) {
                        String storedPassword = (String) user.get("password");
                        if (password.equals(storedPassword)) {
                            session.setAttribute(GlobalControllerAdvice.SESSION_USER_KEY, user);
                            log.info("User {} logged in successfully", username);
                            return "redirect:/home";
                        }
                    }
                }
            }

            log.warn("Failed login attempt for username: {}", username);
            return "redirect:/login?error";

        } catch (Exception ex) {
            log.error("Login error for username: {}", username, ex);
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
                               @RequestParam(value = "error", required = false) String error) {

        if (error != null) {
            model.addAttribute("errorMessage", "Registration failed. Please try again.");
        }

        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());

        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String name,
                          @RequestParam String phone,
                          Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match");
            return "register";
        }

        if (password.length() < 8) {
            model.addAttribute("errorMessage", "Password must be at least 8 characters long");
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
                    apiUrl + "/users",
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("New user registered: {} ({})", username, email);
                return "redirect:/login?message=Registration successful! Please login.";
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
