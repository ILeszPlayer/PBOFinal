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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
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
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getComplaintsAsync(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        if (size > 50) size = 50;
        Page<Complaint> complaints = complaintRepository.findAllByOrderByUpvotesCountDesc(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "upvotesCount")));

        Long currentUserId = null;
        if (authentication != null) {
            try {
                Pengguna currentUser = penggunaRepository.findByEmail(authentication.getName()).orElse(null);
                if (currentUser != null) currentUserId = currentUser.getId();
            } catch (Exception ignored) {}
        }

        List<Map<String, Object>> complaintList = new ArrayList<>();
        Long uid = currentUserId;
        for (Complaint c : complaints.getContent()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", c.getId());
            item.put("judul", c.getJudul());
            item.put("isiPengaduan", c.getIsiPengaduan());
            item.put("kategori", c.getKategori() != null ? c.getKategori().name() : "UMUM");
            item.put("status", c.getStatus() != null ? c.getStatus().name() : "PENDING");
            item.put("urgency", c.getUrgency() != null ? c.getUrgency().name() : "RENDAH");
            item.put("buktiFoto", c.getBuktiFoto());
            item.put("upvotesCount", c.getUpvotesCount());
            item.put("isAnonymous", c.isIsAnonymous());
            item.put("upvoted", uid != null && c.getUpvotedUserIds().contains(uid));
            item.put("tanggal", List.of(c.getTanggal().getYear(), c.getTanggal().getMonthValue(),
                c.getTanggal().getDayOfMonth(), c.getTanggal().getHour(), c.getTanggal().getMinute(), c.getTanggal().getSecond()));

            if (c.getUser() != null && !c.isIsAnonymous()) {
                Map<String, Object> userMap = new LinkedHashMap<>();
                userMap.put("nama", c.getUser().getNama());
                item.put("user", userMap);
            } else {
                item.put("user", null);
            }

            int commentCount = commentRepository.findByComplaintIdOrderByTanggalAsc(c.getId()).size();
            item.put("commentCount", commentCount);

            complaintList.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", complaintList);
        result.put("page", complaints.getNumber());
        result.put("size", complaints.getSize());
        result.put("totalElements", complaints.getTotalElements());
        result.put("totalPages", complaints.getTotalPages());
        result.put("last", complaints.isLast());
        result.put("first", complaints.isFirst());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/complaints/{id}")
    public ResponseEntity<Map<String, Object>> getComplaint(@PathVariable Long id) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            // Load complaint via native query - no lazy loading at all
            List<Object[]> complaintRows = complaintRepository.findComplaintRawDataById(id);
            if (complaintRows.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Pengaduan tidak ditemukan"));
            }
            Object[] row = complaintRows.get(0);
            result.put("id", row[0]);
            result.put("judul", row[1]);
            result.put("isiPengaduan", row[2]);
            result.put("kategori", row[3] != null ? row[3].toString() : "UMUM");
            result.put("status", row[4] != null ? row[4].toString() : "PENDING");
            result.put("urgency", row[5] != null ? row[5].toString() : "RENDAH");
            result.put("buktiFoto", row[6]);
            result.put("upvotesCount", row[7] != null ? ((Number) row[7]).intValue() : 0);
            if (row[8] instanceof Boolean) {
                result.put("isAnonymous", (Boolean) row[8]);
            } else {
                result.put("isAnonymous", row[8] != null && ((Number) row[8]).intValue() == 1);
            }
            // Format tanggal
            if (row[9] instanceof java.sql.Timestamp ts) {
                LocalDateTime ldt = ts.toLocalDateTime();
                result.put("tanggal", List.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(),
                    ldt.getHour(), ldt.getMinute(), ldt.getSecond()));
            } else if (row[9] != null) {
                result.put("tanggal", row[9].toString());
            } else {
                result.put("tanggal", null);
            }
            // User info
            if (row[10] != null) {
                Map<String, Object> userMap = new LinkedHashMap<>();
                userMap.put("nama", row[10].toString());
                result.put("user", userMap);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Gagal memuat detail pengaduan: " + e.getMessage()));
        }

        // Load comments (separate native query)
        try {
            List<Object[]> rows = commentRepository.findCommentRawDataByComplaintId(id);
            List<Map<String, Object>> commentsData = new ArrayList<>();
            for (Object[] cr : rows) {
                Map<String, Object> cmMap = new LinkedHashMap<>();
                cmMap.put("id", cr[0]);
                cmMap.put("isiKomentar", cr[1]);
                if (cr[2] instanceof java.sql.Timestamp ts) {
                    LocalDateTime ldt = ts.toLocalDateTime();
                    cmMap.put("tanggal", List.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(),
                        ldt.getHour(), ldt.getMinute(), ldt.getSecond()));
                } else {
                    cmMap.put("tanggal", cr[2]);
                }
                Map<String, Object> userMap = new LinkedHashMap<>();
                userMap.put("nama", cr[3] != null ? cr[3].toString() : "Warga");
                cmMap.put("user", userMap);
                commentsData.add(cmMap);
            }
            result.put("comments", commentsData);
        } catch (Exception e) {
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

        boolean alreadyUpvoted = complaint.getUpvotedUserIds().contains(user.getId());

        if (alreadyUpvoted) {
            // Toggle: remove upvote
            complaint.getUpvotedUserIds().remove(user.getId());
            complaint.setUpvotesCount(Math.max(0, complaint.getUpvotesCount() - 1));
        } else {
            complaint.getUpvotedUserIds().add(user.getId());
            complaint.setUpvotesCount(complaint.getUpvotesCount() + 1);
        }
        complaintRepository.save(complaint);
        return ResponseEntity.ok(Map.of("success", true, "count", complaint.getUpvotesCount(), "upvoted", !alreadyUpvoted));
    }

    @Transactional
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