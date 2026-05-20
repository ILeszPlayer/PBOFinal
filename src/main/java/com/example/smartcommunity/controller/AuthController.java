package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.RegisterCitizenRequest;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.repository.UserRepository;
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

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedUser") != null) {
            return redirectDashboard(session);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String login, @RequestParam String password,
                        HttpSession session, Model model) {
        Optional<User> userOptional = userRepository.findByEmail(login);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByNama(login);
        }
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Akun tidak ditemukan");
            return "login";
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Password salah");
            return "login";
        }
        session.setAttribute("loggedUser", user);
        session.setAttribute("loggedUserId", user.getId());
        session.setAttribute("loggedUserName", user.getNama());
        session.setMaxInactiveInterval(30 * 60);

        return redirectDashboard(session);
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
            model.addAttribute("success", "Pendaftaran berhasil! Silakan login.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Gagal mendaftar: " + e.getMessage());
            model.addAttribute("registerRequest", request);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String redirectDashboard(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user instanceof com.example.smartcommunity.model.Admin) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/home";
    }
}
