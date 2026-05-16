package com.example.smartcommunity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.ServiceRequestRepository;
import com.example.smartcommunity.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public HomeController(
            UserRepository userRepository,
            ComplaintRepository complaintRepository,
            ServiceRequestRepository serviceRequestRepository
    ) {
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/";
        }

        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalComplaints", complaintRepository.count());
        model.addAttribute("totalServices", serviceRequestRepository.count());

        model.addAttribute("loggedUserName", session.getAttribute("loggedUserName"));
        model.addAttribute("loggedUserRole", session.getAttribute("loggedUserRole"));

        return "dashboard";
    }
}