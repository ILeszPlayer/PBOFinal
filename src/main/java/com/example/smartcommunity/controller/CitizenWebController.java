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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            @RequestParam(value = "kategori", required = false) String kategori,
            @RequestParam(value = "buktiFoto", required = false) MultipartFile buktiFoto,
            @RequestParam(value = "isAnonymous", defaultValue = "false") boolean isAnonymous,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "lokasiNama", required = false) String lokasiNama,
            Authentication authentication) {
        String email = authentication.getName();
        Pengguna user = penggunaService.findByEmail(email);

        CreateComplaintRequest request = new CreateComplaintRequest();
        request.setUserId(user.getId());
        request.setJudul(judul);
        request.setIsiPengaduan(isiPengaduan);
        request.setKategori(kategori);
        request.setBuktiFoto(buktiFoto);
        request.setIsAnonymous(isAnonymous);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setLokasiNama(lokasiNama);

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
    public String viewDetail(@PathVariable Long id, Model model) {
        Complaint complaint = complaintService.findById(id);
        model.addAttribute("complaint", complaint);

        // Load comments via native query to avoid lazy-loading issues
        List<Object[]> rows = commentRepository.findCommentRawDataByComplaintId(id);
        List<Map<String, Object>> commentsList = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        for (Object[] row : rows) {
            Map<String, Object> cm = new java.util.LinkedHashMap<>();
            cm.put("id", row[0]);
            cm.put("isiKomentar", row[1]);
            // Convert SQL timestamp to formatted string
            if (row[2] instanceof java.sql.Timestamp ts) {
                cm.put("tanggalStr", ts.toLocalDateTime().format(dtf));
            } else if (row[2] instanceof LocalDateTime ldt) {
                cm.put("tanggalStr", ldt.format(dtf));
            } else {
                cm.put("tanggalStr", String.valueOf(row[2]));
            }
            Map<String, Object> u = new java.util.LinkedHashMap<>();
            u.put("nama", row[3] != null ? row[3] : "Warga");
            cm.put("user", u);
            commentsList.add(cm);
        }
        model.addAttribute("comments", commentsList);
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

    @PostMapping("/complaint/{id}/delete")
    public String deleteOwnComplaint(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Pengguna user = penggunaService.findByEmail(email);
        Complaint complaint = complaintService.findById(id);
        if (!complaint.getUser().getId().equals(user.getId())) {
            return "redirect:/citizen/profile?error=Bukan pengaduan Anda";
        }
        if (complaint.getStatus() == Complaint.Status.SELESAI) {
            return "redirect:/citizen/profile?error=Tidak bisa menghapus pengaduan yang sudah selesai";
        }
        complaintService.deleteComplaint(id);
        return "redirect:/citizen/profile?deleted=true";
    }

    @GetMapping("/api/my-complaints")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getMyComplaints(Authentication authentication) {
        String email = authentication.getName();
        Pengguna user = penggunaService.findByEmail(email);
        List<Complaint> complaints = complaintService.getComplaintsByUser(user.getId());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Complaint c : complaints) {
            Map<String, Object> item = new java.util.LinkedHashMap<>();
            item.put("id", c.getId());
            item.put("judul", c.getJudul());
            item.put("status", c.getStatus() != null ? c.getStatus().name() : "PENDING");
            item.put("upvotesCount", c.getUpvotesCount());
            item.put("tanggal", c.getTanggal() != null ? List.of(c.getTanggal().getYear(), c.getTanggal().getMonthValue(), c.getTanggal().getDayOfMonth()) : null);
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }
}