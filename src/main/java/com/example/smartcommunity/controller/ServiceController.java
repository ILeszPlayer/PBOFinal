package com.example.smartcommunity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.smartcommunity.model.ServiceRequest;
import com.example.smartcommunity.service.ServiceRequestService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ServiceController {

    private final ServiceRequestService serviceRequestService;

    public ServiceController(ServiceRequestService serviceRequestService){
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/services")
    public String services(HttpSession session, Model model){

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/";
        }

        model.addAttribute("serviceRequest", new ServiceRequest());
        model.addAttribute("services", serviceRequestService.getAllServices());

        model.addAttribute("loggedUserName", session.getAttribute("loggedUserName"));
        model.addAttribute("loggedUserRole", session.getAttribute("loggedUserRole"));

        return "services";
    }

    @PostMapping("/services/save")
    public String saveService(
            @ModelAttribute ServiceRequest serviceRequest,
            HttpSession session
    ){
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/";
        }

        serviceRequest.setStatus("Menunggu");
        serviceRequestService.saveService(serviceRequest);

        return "redirect:/services";
    }
}