package com.example.smartcommunity.controller;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.smartcommunity.model.User;
import com.example.smartcommunity.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String loginPage(HttpSession session, Model model) {

        if (session.getAttribute("loggedUser") != null) {
            return "redirect:/dashboard";
        }

        return "index";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String login,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Optional<User> userOptional = userRepository.findByEmail(login);

        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByNama(login);
        }

        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Akun tidak ditemukan");
            return "index";
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Password salah");
            return "index";
        }

        // Simpan data login ke session
        session.setAttribute("loggedUser", user);
        session.setAttribute("loggedUserId", user.getId());
        session.setAttribute("loggedUserName", user.getNama());
        session.setAttribute("loggedUserRole", user.getRole());

        // Session aktif selama 30 menit
        session.setMaxInactiveInterval(30 * 60);

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}