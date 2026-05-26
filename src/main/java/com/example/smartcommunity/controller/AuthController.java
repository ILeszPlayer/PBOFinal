package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.RegisterCitizenRequest;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.service.PenggunaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final PenggunaRepository penggunaRepository;
    private final PenggunaService penggunaService;

    public AuthController(PenggunaRepository penggunaRepository,
                          PenggunaService penggunaService) {
        this.penggunaRepository = penggunaRepository;
        this.penggunaService = penggunaService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
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
        if (penggunaRepository.existsByEmail(request.getEmail())) {
            model.addAttribute("error", "Email sudah terdaftar");
            model.addAttribute("registerRequest", request);
            return "register";
        }
        try {
            penggunaService.registerCitizen(request);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Gagal mendaftar: " + e.getMessage());
            model.addAttribute("registerRequest", request);
            return "register";
        }
    }
}
