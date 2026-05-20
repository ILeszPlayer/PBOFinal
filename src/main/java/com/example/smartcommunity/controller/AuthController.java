package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.RegisterCitizenRequest;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.repository.UserRepository;
import com.example.smartcommunity.service.NotificationService;
import com.example.smartcommunity.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final NotificationService notificationService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          UserService userService,
                          NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("loggedUserId") != null) {
            return redirectByRole(session);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session, Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Akun tidak ditemukan");
            return "login";
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Password salah");
            return "login";
        }
        session.setAttribute("loggedUserId", user.getId());
        session.setAttribute("loggedUserName", user.getNama());
        session.setAttribute("loggedUserRole", user.getRole());
        return redirectByRole(session);
    }

    @GetMapping("/logout")
    public String logoutGet(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logoutPost(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterCitizenRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid RegisterCitizenRequest request,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("registerRequest", request);
            return "register";
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            model.addAttribute("error", "Email sudah terdaftar");
            model.addAttribute("registerRequest", request);
            return "register";
        }
        try {
            userService.registerCitizen(request);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Gagal mendaftar: " + e.getMessage());
            model.addAttribute("registerRequest", request);
            return "register";
        }
    }

    private String redirectByRole(HttpSession session) {
        String role = (String) session.getAttribute("loggedUserRole");
        if ("ADMIN".equals(role)) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/citizen/home";
    }
}
