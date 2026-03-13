package com.amin.aggar.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/login")
    public String loginPage(Model model,
                           @RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        
        // Add required attributes for header fragment
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       Model model) {
        
        // TODO: Implement actual authentication logic
        // For now, just a simple demonstration
        if ("admin".equals(username) && "password".equals(password)) {
            log.info("User {} logged in successfully", username);
            return "redirect:/home";
        } else {
            log.warn("Failed login attempt for username: {}", username);
            return "redirect:/login?error";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        log.info("User logged out");
        return "redirect:/login?logout";
    }

    @GetMapping("/register")
    public String registerPage(Model model,
                               @RequestParam(value = "error", required = false) String error) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "Registration failed. Please try again.");
        }
        
        // Add required attributes for header fragment
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String phone,
                          Model model) {
        
        // Basic validation
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match");
            return "register";
        }
        
        if (password.length() < 8) {
            model.addAttribute("errorMessage", "Password must be at least 8 characters long");
            return "register";
        }
        
        // TODO: Implement actual registration logic
        // For now, just a simple demonstration
        try {
            log.info("New user registration: {} ({})", username, email);
            // Simulate successful registration
            return "redirect:/login?message=Registration successful! Please login.";
        } catch (Exception ex) {
            log.error("Registration failed for username: {}", username, ex);
            return "redirect:/register?error";
        }
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        // TODO: Get actual user data from session/database
        // For now, using mock data
        model.addAttribute("user", getMockUser());
        model.addAttribute("savedProperties", getMockSavedProperties());
        model.addAttribute("recentSearches", getMockRecentSearches());
        
        // Add required attributes for header fragment
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam String phone,
                              Model model) {
        
        // TODO: Update user profile in database
        log.info("Profile updated for user: {} {} ({})", firstName, lastName, email);
        
        model.addAttribute("successMessage", "Profile updated successfully!");
        model.addAttribute("user", getMockUser());
        model.addAttribute("savedProperties", getMockSavedProperties());
        model.addAttribute("recentSearches", getMockRecentSearches());
        
        // Add required attributes for header fragment
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        
        return "profile";
    }

    // Mock data methods (TODO: Replace with actual database calls)
    private Object getMockUser() {
        return java.util.Map.of(
            "firstName", "John",
            "lastName", "Doe",
            "email", "john.doe@example.com",
            "phone", "+1 (555) 123-4567",
            "username", "johndoe",
            "memberSince", "January 15, 2024"
        );
    }

    private java.util.List<Object> getMockSavedProperties() {
        return java.util.List.of(
            java.util.Map.of(
                "id", 1,
                "title", "Modern Downtown Apartment",
                "price", "$450,000",
                "address", "123 Main St, New York, NY",
                "image", "https://via.placeholder.com/300x200/007bff/ffffff?text=Property+1"
            ),
            java.util.Map.of(
                "id", 2,
                "title", "Spacious Family Home",
                "price", "$750,000",
                "address", "456 Oak Ave, Brooklyn, NY",
                "image", "https://via.placeholder.com/300x200/28a745/ffffff?text=Property+2"
            )
        );
    }

    private java.util.List<Object> getMockRecentSearches() {
        return java.util.List.of(
            "Apartments in Manhattan, $300k-$500k",
            "Houses in Brooklyn, 3+ bedrooms",
            "Condos in Queens, under $400k"
        );
    }
}
