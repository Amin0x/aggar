package com.amin.aggar.frontend.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    public static final String SESSION_USER_KEY = "loggedInUser";

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        Object user = session.getAttribute(SESSION_USER_KEY);
        model.addAttribute("isAuthenticated", user != null);
        model.addAttribute("currentUser", user);
    }
}
