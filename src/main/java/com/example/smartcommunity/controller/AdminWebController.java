package com.example.smartcommunity.controller;

import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Broadcast;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.service.BroadcastService;
import com.example.smartcommunity.service.PenggunaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private PenggunaService userService;

    private String mapIncomingStatus(String status) {
        if (status == null) return "PENDING";
        return switch (status.toUpperCase()) {
            case "PENDING" -> "PENDING";
            case "PROSES", "PROCESSED" -> "PROSES";
            case "SELESAI", "RESOLVED" -> "SELESAI";
            default -> "PENDING";
        };
    }

    @GetMapping("/dashboard")
    public String viewDashboardMetrics(
            @RequestParam(value = "filter", required = false) String filter,
            Authentication authentication,
            Model model) {

        try {
            if (authentication != null) {
                model.addAttribute("adminUser", authentication.getName());
            } else {
                model.addAttribute("adminUser", "Administrator");
            }

            long total = complaintRepository.count();
            long pending = complaintRepository.countByStatus("PENDING");
            long processed = complaintRepository.countByStatus("PROSES");
            long resolved = complaintRepository.countByStatus("SELESAI");

            model.addAttribute("totalComplaints", total);
            model.addAttribute("pendingCount", pending);
            model.addAttribute("processedCount", processed);
            model.addAttribute("resolvedCount", resolved);

            List<Object[]> chartData = complaintRepository.getMonthlyResolutionStats();
            List<Integer> months = new ArrayList<>();
            List<Long> counts = new ArrayList<>();

            if (chartData != null) {
                for (Object[] row : chartData) {
                    if (row[0] != null && row[1] != null) {
                        months.add(((Number) row[0]).intValue());
                        counts.add(((Number) row[1]).longValue());
                    }
                }
            }
            model.addAttribute("chartMonths", months);
            model.addAttribute("chartCounts", counts);

            long maxCount = counts.stream().max(Long::compare).orElse(1L);
            model.addAttribute("maxCount", maxCount);

            model.addAttribute("complaints", complaintRepository.findAllByOrderByTanggalDesc());

        } catch (Exception e) {
            model.addAttribute("totalComplaints", 0L);
            model.addAttribute("pendingCount", 0L);
            model.addAttribute("processedCount", 0L);
            model.addAttribute("resolvedCount", 0L);
            model.addAttribute("chartMonths", new ArrayList<>());
            model.addAttribute("chartCounts", new ArrayList<>());
            model.addAttribute("maxCount", 1L);
            model.addAttribute("complaints", new ArrayList<>());
            System.err.println("Fallback metrics triggered securely: " + e.getMessage());
        }

        return "admin-dashboard";
    }

    @Transactional(readOnly = true)
    @GetMapping("/complaints/manage")
    public String manageComplaintsMenu(Model model) {
        model.addAttribute("complaints", complaintRepository.findAll());
        return "admin-manage-complaints";
    }

    @PostMapping("/complaints/{id}/update-status")
    public String processStatusMutation(@PathVariable Long id, @RequestParam("status") String status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Target complaint structure missing"));
        String mappedStatus = mapIncomingStatus(status);
        complaint.setStatus(Complaint.Status.valueOf(mappedStatus));
        complaintRepository.save(complaint);
        return "redirect:/admin/complaints/manage?mutationSuccess=true";
    }

    @PostMapping("/broadcast/submit")
    public String submitBroadcast(@ModelAttribute Broadcast broadcast) {
        broadcastService.save(broadcast);
        return "redirect:/admin/complaints/manage?broadcastSuccess=true";
    }

    @GetMapping("/warga")
    public String viewWargaMenu(Model model) {
        model.addAttribute("users", userService.findAllCitizens());
        return "admin-users";
    }
}
