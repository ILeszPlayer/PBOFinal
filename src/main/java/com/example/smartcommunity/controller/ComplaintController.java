package com.example.smartcommunity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.service.ComplaintService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService){
        this.complaintService = complaintService;
    }

    @GetMapping("/complaints")
    public String complaints(HttpSession session, Model model){

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/";
        }

        model.addAttribute("complaint", new Complaint());
        model.addAttribute("complaints", complaintService.getAllComplaints());

        model.addAttribute("loggedUserName", session.getAttribute("loggedUserName"));
        model.addAttribute("loggedUserRole", session.getAttribute("loggedUserRole"));

        return "complaints";
    }

    @PostMapping("/complaints/save")
    public String saveComplaint(
            @ModelAttribute Complaint complaint,
            HttpSession session
    ){
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/";
        }

        complaint.setStatus("Diproses");
        complaintService.saveComplaint(complaint);

        return "redirect:/complaints";
    }
}