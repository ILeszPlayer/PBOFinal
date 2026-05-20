package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.CreateBroadcastRequest;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.service.BroadcastService;
import com.example.smartcommunity.service.ComplaintService;
import com.example.smartcommunity.service.NotificationService;
import com.example.smartcommunity.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ComplaintService complaintService;
    private final UserService userService;
    private final BroadcastService broadcastService;
    private final NotificationService notificationService;

    public AdminController(ComplaintService complaintService,
                           UserService userService,
                           BroadcastService broadcastService,
                           NotificationService notificationService) {
        this.complaintService = complaintService;
        this.userService = userService;
        this.broadcastService = broadcastService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String kategori,
                            @RequestParam(required = false) String urgency,
                            HttpSession session, Model model) {
        if (session.getAttribute("loggedUserId") == null) {
            return "redirect:/login";
        }

        long total = complaintService.countAll();
        long pending = complaintService.countByStatus(Complaint.Status.PENDING);
        long process = complaintService.countByStatus(Complaint.Status.PROSES);
        long completed = complaintService.countByStatus(Complaint.Status.SELESAI);
        long users = userService.countUsers();
        double avgSla = complaintService.getAverageSlaHours();

        model.addAttribute("totalComplaints", total);
        model.addAttribute("pendingCount", pending);
        model.addAttribute("processCount", process);
        model.addAttribute("completedCount", completed);
        model.addAttribute("totalUsers", users);
        model.addAttribute("avgSlaHours", String.format("%.1f", avgSla));

        List<Complaint> complaints;
        if (kategori != null && !kategori.isEmpty()) {
            Complaint.Kategori kat = Complaint.Kategori.valueOf(kategori);
            complaints = complaintService.getComplaintsByCategory(kat);
        } else if (urgency != null && !urgency.isEmpty()) {
            Complaint.Urgency urg = Complaint.Urgency.valueOf(urgency);
            complaints = complaintService.getComplaintsByUrgency(urg);
        } else if (search != null && !search.trim().isEmpty()) {
            complaints = complaintService.searchComplaints(search);
        } else {
            complaints = complaintService.getAllComplaints();
        }
        model.addAttribute("complaints", complaints);
        model.addAttribute("search", search);
        model.addAttribute("selectedKategori", kategori);
        model.addAttribute("selectedUrgency", urgency);
        model.addAttribute("kategoriValues", Complaint.Kategori.values());
        model.addAttribute("urgencyValues", Complaint.Urgency.values());

        Map<String, Long> categoryCounts = new LinkedHashMap<>();
        List<Object[]> counts = complaintService.countByKategori();
        for (Complaint.Kategori k : Complaint.Kategori.values()) {
            categoryCounts.put(k.getDisplayName(), 0L);
        }
        for (Object[] row : counts) {
            categoryCounts.put(((Complaint.Kategori) row[0]).getDisplayName(), (Long) row[1]);
        }
        model.addAttribute("categoryCounts", categoryCounts);

        Map<Integer, Long> monthlyResolved = new LinkedHashMap<>();
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        for (int m = 1; m <= currentMonth; m++) {
            monthlyResolved.put(m, 0L);
        }
        List<Object[]> resolvedByMonth = complaintService.countResolvedByMonth();
        for (Object[] row : resolvedByMonth) {
            Integer month = (Integer) row[0];
            Long count = (Long) row[1];
            if (monthlyResolved.containsKey(month)) {
                monthlyResolved.put(month, count);
            }
        }
        model.addAttribute("monthlyResolved", monthlyResolved);

        Map<String, Long> urgencyCounts = new LinkedHashMap<>();
        List<Object[]> urgencyData = complaintService.countByUrgency();
        for (Complaint.Urgency u : Complaint.Urgency.values()) {
            urgencyCounts.put(u.getDisplayName(), 0L);
        }
        for (Object[] row : urgencyData) {
            urgencyCounts.put(((Complaint.Urgency) row[0]).getDisplayName(), (Long) row[1]);
        }
        model.addAttribute("urgencyCounts", urgencyCounts);

        List<Complaint> geoComplaints = complaintService.getGeoTaggedComplaints();
        model.addAttribute("geoComplaints", geoComplaints);

        model.addAttribute("broadcastRequest", new CreateBroadcastRequest());

        return "admin-dashboard";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long complaintId,
                               @RequestParam Complaint.Status status,
                               RedirectAttributes redirectAttributes) {
        complaintService.updateStatus(complaintId, status);
        redirectAttributes.addFlashAttribute("success", "Status pengaduan berhasil diperbarui!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/broadcast")
    public String sendBroadcast(@ModelAttribute CreateBroadcastRequest request,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        request.setAdminId((Long) session.getAttribute("loggedUserId"));
        broadcastService.createBroadcast(request);
        redirectAttributes.addFlashAttribute("success", "Broadcast berhasil dikirim!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/export")
    public String export(Model model) {
        model.addAttribute("complaints", complaintService.getAllComplaints());
        return "admin-export";
    }
}
