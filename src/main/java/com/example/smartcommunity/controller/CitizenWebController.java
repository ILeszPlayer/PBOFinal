package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.model.Comment;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.repository.CommentRepository;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.service.BroadcastService;
import com.example.smartcommunity.service.ComplaintService;
import com.example.smartcommunity.service.PenggunaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/citizen")
public class CitizenWebController {

    @Autowired private BroadcastService broadcastService;
    @Autowired private ComplaintService complaintService;
    @Autowired private PenggunaService penggunaService;
    @Autowired private ComplaintRepository complaintRepository;
    @Autowired private CommentRepository commentRepository;

    @GetMapping("/home")
    public String renderHomeSkeleton(Authentication authentication, Model model) {
        model.addAttribute("broadcasts", broadcastService.findAllActive());
        if (authentication != null) {
            try {
                Pengguna user = penggunaService.findByEmail(authentication.getName());
                model.addAttribute("userName", user.getNama());
                model.addAttribute("userPoints", user.getReputationPoints());
                model.addAttribute("userTier", user.getReputationTier());
            } catch (Exception ignored) {}
        }
        return "citizen-home";
    }

    @PostMapping("/create-complaint")
    public String submitComplaint(
            @RequestParam("judul") String judul,
            @RequestParam("isiPengaduan") String isiPengaduan,
            @RequestParam(value = "buktiFoto", required = false) MultipartFile buktiFoto,
            @RequestParam(value = "isAnonymous", defaultValue = "false") boolean isAnonymous,
            Authentication authentication) {
        String email = authentication.getName();
        Pengguna user = penggunaService.findByEmail(email);

        CreateComplaintRequest request = new CreateComplaintRequest();
        request.setUserId(user.getId());
        request.setJudul(judul);
        request.setIsiPengaduan(isiPengaduan);
        request.setBuktiFoto(buktiFoto);
        request.setIsAnonymous(isAnonymous);

        complaintService.createComplaint(request);
        return "redirect:/citizen/home?submitted=true";
    }

    @GetMapping("/profile")
    @Transactional(readOnly = true)
    public String viewProfile(Authentication authentication, Model model) {
        String email = authentication.getName();
        Pengguna user = penggunaService.findByEmail(email);

        List<Complaint> myComplaints = complaintService.getComplaintsByUser(user.getId());
        int totalUpvotes = myComplaints.stream().mapToInt(Complaint::getUpvotesCount).sum();

        int points = user.getReputationPoints();
        String currentTier = user.getReputationTier();
        int nextTierPoints;
        String nextTierName;
        if (points >= 200) {
            nextTierPoints = points;
            nextTierName = "Pahlawan Komunitas (Maks)";
        } else if (points >= 100) {
            nextTierPoints = 200;
            nextTierName = "Pahlawan Komunitas";
        } else if (points >= 50) {
            nextTierPoints = 100;
            nextTierName = "Warga Teladan";
        } else if (points >= 20) {
            nextTierPoints = 50;
            nextTierName = "Warga Peduli";
        } else {
            nextTierPoints = 20;
            nextTierName = "Warga Aktif";
        }
        int tierProgressPercent = (int) ((double) points / nextTierPoints * 100);
        if (tierProgressPercent > 100) tierProgressPercent = 100;

        model.addAttribute("user", user);
        model.addAttribute("myComplaints", myComplaints);
        model.addAttribute("totalUpvotes", totalUpvotes);
        model.addAttribute("reputationTier", currentTier);
        model.addAttribute("tierProgressPercent", tierProgressPercent);
        model.addAttribute("nextTierPoints", nextTierPoints);
        model.addAttribute("nextTierName", nextTierName);
        return "citizen-profile";
    }

    @GetMapping("/detail/{id}")
    @Transactional(readOnly = true)
    public String viewDetail(@PathVariable Long id, Model model) {
        Complaint complaint = complaintService.findById(id);
        model.addAttribute("complaint", complaint);
        model.addAttribute("comments", complaint.getComments());
        return "citizen-detail";
    }

    @PostMapping("/comment")
    public String submitComment(
            @RequestParam("complaintId") Long complaintId,
            @RequestParam("isiKomentar") String isiKomentar,
            Authentication authentication) {
        String email = authentication.getName();
        Pengguna user = penggunaService.findByEmail(email);
        Complaint complaint = complaintService.findById(complaintId);

        Comment comment = new Comment(isiKomentar, user, complaint);
        comment.setTanggal(LocalDateTime.now());
        commentRepository.save(comment);

        return "redirect:/citizen/detail/" + complaintId + "?commented=true";
    }

    @PostMapping("/upvote/{id}")
    public String upvoteFromDetail(@PathVariable Long id) {
        complaintService.upvote(id);
        return "redirect:/citizen/detail/" + id;
    }
}