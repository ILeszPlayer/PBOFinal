package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.CreateCommentRequest;
import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.model.*;
import com.example.smartcommunity.service.CommentService;
import com.example.smartcommunity.service.ComplaintService;
import com.example.smartcommunity.service.NotificationService;
import com.example.smartcommunity.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/citizen")
public class CitizenController {

    private final ComplaintService complaintService;
    private final UserService userService;
    private final CommentService commentService;
    private final NotificationService notificationService;

    public CitizenController(ComplaintService complaintService,
                             UserService userService,
                             CommentService commentService,
                             NotificationService notificationService) {
        this.complaintService = complaintService;
        this.userService = userService;
        this.commentService = commentService;
        this.notificationService = notificationService;
    }

    @GetMapping("/home")
    public String home(@RequestParam(required = false) String search,
                       @RequestParam(required = false) String kategori,
                       @RequestParam(required = false) String urgency,
                       HttpSession session, Model model) {
        if (session.getAttribute("loggedUserId") == null) {
            return "redirect:/login";
        }

        User user = userService.findById((Long) session.getAttribute("loggedUserId"));
        model.addAttribute("user", user);
        model.addAttribute("reputationTier", user.getReputationTier());
        model.addAttribute("unreadCount", notificationService.countUnread(user.getId()));

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

        List<Complaint> geoComplaints = complaintService.getGeoTaggedComplaints();
        model.addAttribute("geoComplaints", geoComplaints);

        List<Notification> latestNotifications = notificationService.getNotificationsByUser(user.getId());
        if (latestNotifications.size() > 5) {
            latestNotifications = latestNotifications.subList(0, 5);
        }
        model.addAttribute("latestNotifications", latestNotifications);

        return "citizen-home";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        if (session.getAttribute("loggedUserId") == null) {
            return "redirect:/login";
        }
        User user = userService.findById((Long) session.getAttribute("loggedUserId"));
        model.addAttribute("user", user);
        model.addAttribute("reputationTier", user.getReputationTier());

        int rep = user.getReputationPoints();
        int nextTier;
        String nextTierName;
        if (rep < 20) {
            nextTier = 20;
            nextTierName = "Warga Aktif";
        } else if (rep < 50) {
            nextTier = 50;
            nextTierName = "Warga Peduli";
        } else if (rep < 100) {
            nextTier = 100;
            nextTierName = "Warga Teladan";
        } else if (rep < 200) {
            nextTier = 200;
            nextTierName = "Pahlawan Komunitas";
        } else {
            nextTier = rep;
            nextTierName = "Pahlawan Komunitas (Max)";
        }
        model.addAttribute("nextTierPoints", nextTier);
        model.addAttribute("nextTierName", nextTierName);
        model.addAttribute("tierProgressPercent", Math.min(100, Math.max(0, (rep * 100) / Math.max(1, nextTier))));

        List<Complaint> myComplaints = complaintService.getComplaintsByUser(user.getId());
        model.addAttribute("myComplaints", myComplaints);

        int totalUpvotes = myComplaints.stream().mapToInt(Complaint::getUpvotesCount).sum();
        model.addAttribute("totalUpvotes", totalUpvotes);

        model.addAttribute("unreadCount", notificationService.countUnread(user.getId()));

        return "citizen-profile";
    }

    @PostMapping("/create-complaint")
    public String createComplaint(@ModelAttribute CreateComplaintRequest request,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedUserId") == null) {
            return "redirect:/login";
        }
        request.setUserId((Long) session.getAttribute("loggedUserId"));
        complaintService.createComplaint(request);
        redirectAttributes.addFlashAttribute("success", "Pengaduan berhasil dibuat! (+10 poin reputasi)");
        return "redirect:/citizen/home";
    }

    @PostMapping("/upvote/{complaintId}")
    public String upvote(@PathVariable Long complaintId, RedirectAttributes redirectAttributes) {
        Complaint complaint = complaintService.upvote(complaintId);
        redirectAttributes.addFlashAttribute("success",
                "Upvote berhasil! Total: " + complaint.getUpvotesCount());
        return "redirect:/citizen/home";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("loggedUserId") == null) {
            return "redirect:/login";
        }
        Complaint complaint = complaintService.findById(id);
        model.addAttribute("complaint", complaint);
        model.addAttribute("comments", commentService.getCommentsByComplaint(id));
        model.addAttribute("commentRequest", new CreateCommentRequest());
        model.addAttribute("unreadCount", notificationService.countUnread((Long) session.getAttribute("loggedUserId")));
        return "citizen-detail";
    }

    @PostMapping("/comment")
    public String addComment(@RequestParam Long complaintId,
                             @RequestParam String isiKomentar,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedUserId") == null) {
            return "redirect:/login";
        }
        CreateCommentRequest request = new CreateCommentRequest();
        request.setComplaintId(complaintId);
        request.setUserId((Long) session.getAttribute("loggedUserId"));
        request.setIsiKomentar(isiKomentar);
        commentService.addComment(request);
        redirectAttributes.addFlashAttribute("success", "Komentar ditambahkan! (+5 poin reputasi)");
        return "redirect:/citizen/detail/" + complaintId;
    }

    @PostMapping("/notifications/read/{id}")
    public String markNotificationRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/citizen/home";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsRead(HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedUserId");
        if (userId != null) {
            notificationService.markAllAsRead(userId);
        }
        return "redirect:/citizen/home";
    }
}
