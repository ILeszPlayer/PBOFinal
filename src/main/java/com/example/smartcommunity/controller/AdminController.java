package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.CreateBroadcastRequest;
import com.example.smartcommunity.model.Admin;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.service.BroadcastService;
import com.example.smartcommunity.service.ComplaintService;
import com.example.smartcommunity.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ComplaintService complaintService;
    private final BroadcastService broadcastService;
    private final UserService userService;

    public AdminController(ComplaintService complaintService,
                           BroadcastService broadcastService,
                           UserService userService) {
        this.complaintService = complaintService;
        this.broadcastService = broadcastService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        long totalComplaints = complaintService.countAll();
        long solvedComplaints = complaintService.countByStatus(Complaint.Status.SELESAI);
        long pendingComplaints = complaintService.countByStatus(Complaint.Status.MENUNGGU);
        long processingComplaints = complaintService.countByStatus(Complaint.Status.DIPROSES);

        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("solvedComplaints", solvedComplaints);
        model.addAttribute("pendingComplaints", pendingComplaints);
        model.addAttribute("processingComplaints", processingComplaints);
        model.addAttribute("complaints", complaintService.getAllComplaints());
        model.addAttribute("broadcasts", broadcastService.getAllBroadcasts());
        model.addAttribute("loggedUserName", session.getAttribute("loggedUserName"));
        return "admin-dashboard";
    }

    @PostMapping("/complaint/status")
    public String updateStatus(@RequestParam Long complaintId,
                               @RequestParam Complaint.Status status,
                               HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        complaintService.updateStatus(complaintId, status);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/broadcast/create")
    public String createBroadcast(@RequestParam String judul,
                                  @RequestParam String isiBroadcast,
                                  HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        CreateBroadcastRequest request = new CreateBroadcastRequest();
        request.setAdminId((Long) session.getAttribute("loggedUserId"));
        request.setJudul(judul);
        request.setIsiBroadcast(isiBroadcast);
        broadcastService.createBroadcast(request);
        return "redirect:/admin/dashboard";
    }

    private boolean isAdmin(HttpSession session) {
        return session.getAttribute("loggedUser") instanceof Admin;
    }
}
