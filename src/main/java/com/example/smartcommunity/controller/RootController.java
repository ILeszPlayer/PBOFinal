package com.example.smartcommunity.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String root(HttpSession session) {
        if (session.getAttribute("loggedUserId") != null) {
            String role = (String) session.getAttribute("loggedUserRole");
            if ("ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/citizen/home";
        }
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String homeRedirect(HttpSession session) {
        if (session.getAttribute("loggedUserId") != null) {
            return "redirect:/citizen/home";
        }
        return "redirect:/login";
    }
}
