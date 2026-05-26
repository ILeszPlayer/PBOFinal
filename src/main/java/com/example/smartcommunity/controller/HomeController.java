package com.example.smartcommunity.controller;

import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.service.BroadcastService;
import com.example.smartcommunity.service.ComplaintService;
import com.example.smartcommunity.service.PenggunaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ComplaintService complaintService;
    private final BroadcastService broadcastService;
    private final PenggunaService penggunaService;

    public HomeController(ComplaintService complaintService,
                          BroadcastService broadcastService,
                          PenggunaService penggunaService) {
        this.complaintService = complaintService;
        this.broadcastService = broadcastService;
        this.penggunaService = penggunaService;
    }

    @GetMapping("/home")
    public String citizenHome(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Pengguna user = penggunaService.findByEmail(email);

        model.addAttribute("user", user);
        model.addAttribute("broadcasts", broadcastService.getAllBroadcasts());
        model.addAttribute("complaints", complaintService.getAllComplaints());

        return "citizen-home";
    }
}
