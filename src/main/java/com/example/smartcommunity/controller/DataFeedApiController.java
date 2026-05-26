package com.example.smartcommunity.controller;

import com.example.smartcommunity.model.Comment;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Notification;
import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.repository.CommentRepository;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DataFeedApiController {

    @Autowired private ComplaintRepository complaintRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private PenggunaRepository penggunaRepository;
    @Autowired private NotificationService notificationService;

    @GetMapping("/feed/complaints")
    public ResponseEntity<?> getComplaintsAsync(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (size > 50) size = 50;
        Page<Complaint> complaints = complaintRepository.findAllByOrderByUpvotesCountDesc(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "upvotesCount")));
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/complaints/{id}")
    public ResponseEntity<Map<String, Object>> getComplaint(@PathVariable Long id) {
        if (!complaintRepository.existsById(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Pengaduan tidak ditemukan"));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);

        try {
            // Use native query to get raw data, no Hibernate entity loading
            List<Object[]> rows = commentRepository.findCommentRawDataByComplaintId(id);
            List<Map<String, Object>> commentsData = new ArrayList<>();
            for (Object[] row : rows) {
                Map<String, Object> cmMap = new LinkedHashMap<>();
                cmMap.put("id", row[0]);                   // c.id
                cmMap.put("isiKomentar", row[1]);           // c.isi_komentar
                // Convert timestamp to [y,M,d,h,m,s] format for JS fmtDate()
                if (row[2] instanceof java.sql.Timestamp ts) {
                    LocalDateTime ldt = ts.toLocalDateTime();
                    cmMap.put("tanggal", List.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(),
                        ldt.getHour(), ldt.getMinute(), ldt.getSecond()));
                } else {
                    cmMap.put("tanggal", row[2]);
                }
                Map<String, Object> userMap = new LinkedHashMap<>();
                userMap.put("nama", row[3] != null ? row[3] : "Warga");  // u.nama
                cmMap.put("user", userMap);
                commentsData.add(cmMap);
            }
            result.put("comments", commentsData);
        } catch (Exception e) {
            result.put("error", "Gagal memuat komentar: " + e.getMessage());
            result.put("comments", List.of());
        }

        return ResponseEntity.ok(result);
    }

    @Transactional
    @PostMapping("/complaints/{id}/upvote")
    public ResponseEntity<?> upvoteComplaint(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Harus login"));
        }
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));
        Pengguna user = penggunaRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Anti-spam: check if user already upvoted
        if (complaint.getUpvotedUserIds().contains(user.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Anda sudah mendukung pengaduan ini"));
        }

        complaint.getUpvotedUserIds().add(user.getId());
        complaint.setUpvotesCount(complaint.getUpvotesCount() + 1);
        complaintRepository.save(complaint);
        return ResponseEntity.ok(Map.of("success", true, "count", complaint.getUpvotesCount()));
    }

    @PostMapping("/complaints/{id}/comment")
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Harus login"));
        }
        String isiKomentar = body.get("isiKomentar");
        if (isiKomentar == null || isiKomentar.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Komentar tidak boleh kosong"));
        }

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));
        Pengguna user = penggunaRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Comment comment = new Comment(isiKomentar, user, complaint);
        comment.setTanggal(LocalDateTime.now());
        commentRepository.save(comment);

        // Notify complaint owner
        if (complaint.getUser() != null && !complaint.getUser().getId().equals(user.getId())) {
            try {
                notificationService.createNotification(
                    Notification.Type.NEW_COMMENT,
                    user.getNama() + " berkomentar pada pengaduan \"" + complaint.getJudul() + "\"",
                    complaint.getUser(),
                    complaint
                );
            } catch (Exception ignored) {}
        }

        int commentCount = commentRepository.findByComplaintIdOrderByTanggalAsc(id).size();

        return ResponseEntity.ok(Map.of(
            "success", true,
            "commentCount", commentCount
        ));
    }
}