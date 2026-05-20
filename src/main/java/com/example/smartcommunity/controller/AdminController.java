package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.CreateBroadcastRequest;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.service.BroadcastService;
import com.example.smartcommunity.service.ComplaintService;
import com.example.smartcommunity.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        Long userId = (Long) session.getAttribute("loggedUserId");
        if (userId == null) return "redirect:/login";

        User admin = userService.findById(userId);

        long totalComplaints = complaintService.countAll();
        long resolvedComplaints = complaintService.countByStatus(Complaint.Status.RESOLVED);
        long pendingComplaints = complaintService.countByStatus(Complaint.Status.PENDING);
        long processedComplaints = complaintService.countByStatus(Complaint.Status.PROCESSED);

        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("resolvedComplaints", resolvedComplaints);
        model.addAttribute("pendingComplaints", pendingComplaints);
        model.addAttribute("processedComplaints", processedComplaints);
        model.addAttribute("complaints", complaintService.getAllComplaints());
        model.addAttribute("broadcasts", broadcastService.getAllBroadcasts());
        model.addAttribute("loggedUserName", admin.getNama());
        return "admin-dashboard";
    }

    @PostMapping("/complaint/status")
    public String updateStatus(@RequestParam Long complaintId,
                               @RequestParam Complaint.Status status,
                               RedirectAttributes redirect) {
        try {
            complaintService.updateStatus(complaintId, status);
            redirect.addFlashAttribute("toast", "Status pengaduan berhasil diperbarui");
            redirect.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("toast", "Gagal memperbarui status: " + e.getMessage());
            redirect.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/complaint/delete")
    public String deleteComplaint(@RequestParam Long complaintId,
                                  RedirectAttributes redirect) {
        try {
            complaintService.deleteComplaint(complaintId);
            redirect.addFlashAttribute("toast", "Pengaduan berhasil dihapus");
            redirect.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("toast", "Gagal menghapus: " + e.getMessage());
            redirect.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/broadcast/create")
    public String createBroadcast(@RequestParam String judul,
                                  @RequestParam String isiBroadcast,
                                  HttpSession session,
                                  RedirectAttributes redirect) {
        try {
            Long userId = (Long) session.getAttribute("loggedUserId");
            User admin = userService.findById(userId);
            CreateBroadcastRequest request = new CreateBroadcastRequest();
            request.setAdminId(admin.getId());
            request.setJudul(judul);
            request.setIsiBroadcast(isiBroadcast);
            broadcastService.createBroadcast(request);
            redirect.addFlashAttribute("toast", "Broadcast berhasil dipublikasikan");
            redirect.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("toast", "Gagal membuat broadcast: " + e.getMessage());
            redirect.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/dashboard";
    }
}
