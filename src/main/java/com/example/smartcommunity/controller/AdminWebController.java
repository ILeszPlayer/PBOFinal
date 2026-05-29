package com.example.smartcommunity.controller;

import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Broadcast;
import com.example.smartcommunity.model.Notification;
import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.model.Poll;
import com.example.smartcommunity.repository.CommentRepository;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.repository.PollRepository;
import com.example.smartcommunity.service.BroadcastService;
import com.example.smartcommunity.service.NotificationService;
import com.example.smartcommunity.service.PenggunaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private PenggunaService userService;

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        if (authentication != null) {
            model.addAttribute("adminUser", authentication.getName());
        } else {
            model.addAttribute("adminUser", "Administrator");
        }

        try {
            long total = complaintRepository.count();
            long pending = complaintRepository.countByStatus(Complaint.Status.PENDING);
            long processed = complaintRepository.countByStatus(Complaint.Status.PROSES);
            long resolved = complaintRepository.countByStatus(Complaint.Status.SELESAI);
            model.addAttribute("totalComplaints", total);
            model.addAttribute("pendingCount", pending);
            model.addAttribute("processedCount", processed);
            model.addAttribute("resolvedCount", resolved);
        } catch (Exception e) {
            System.err.println("Dashboard stats failed: " + e.getMessage());
            model.addAttribute("totalComplaints", 0L);
            model.addAttribute("pendingCount", 0L);
            model.addAttribute("processedCount", 0L);
            model.addAttribute("resolvedCount", 0L);
        }

        // Average SLA hours & compliance rate
        try {
            List<Complaint> resolved = complaintRepository.findByStatus(Complaint.Status.SELESAI);
            double avgSla = resolved.stream().filter(c -> c.getProcessedAt() != null).mapToLong(Complaint::getSlaHours).average().orElse(0);
            model.addAttribute("avgSlaHours", Math.round(avgSla * 10.0) / 10.0);
            long totalResolved = resolved.size();
            long compliantCount = resolved.stream().filter(c -> c.getProcessedAt() != null && c.isSlaCompliant()).count();
            long slaRate = totalResolved > 0 ? Math.round((double) compliantCount / totalResolved * 100) : 0;
            model.addAttribute("slaComplianceRate", slaRate);
        } catch (Exception e) { model.addAttribute("avgSlaHours", 0); model.addAttribute("slaComplianceRate", 0); }

        // Top upvoted complaints
        try {
            List<Complaint> topUpvoted = complaintRepository.findAllByOrderByUpvotesCountDesc().stream().limit(5).toList();
            model.addAttribute("topComplaints", topUpvoted);
        } catch (Exception e) { model.addAttribute("topComplaints", new ArrayList<>()); }

        // Chart data - isolated try/catch so failure doesn't break dashboard
        try {
            List<Object[]> chartData = complaintRepository.getMonthlyStats();
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
            model.addAttribute("maxCount", counts.stream().max(Long::compare).orElse(1L));
        } catch (Exception e) {
            model.addAttribute("chartMonths", new ArrayList<>());
            model.addAttribute("chartCounts", new ArrayList<>());
            model.addAttribute("maxCount", 1L);
            System.err.println("Chart query failed: " + e.getMessage());
        }

        try {
            model.addAttribute("complaints", complaintRepository.findAllByOrderByTanggalDesc());
        } catch (Exception e) {
            model.addAttribute("complaints", new ArrayList<>());
            System.err.println("Complaints query failed: " + e.getMessage());
        }

        return "admin-dashboard";
    }

    @Transactional(readOnly = true)
    @GetMapping("/complaints/manage")
    public String manageComplaintsMenu(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "kategori", required = false) String kategori,
            @RequestParam(value = "status", required = false) String status,
            Model model) {
        List<Complaint> complaints;
        if (search != null && !search.trim().isEmpty()) {
            complaints = complaintRepository.findByJudulContainingIgnoreCaseOrIsiPengaduanContainingIgnoreCase(search.trim(), search.trim());
        } else {
            complaints = complaintRepository.findAll();
        }
        // Apply filters in memory
        if (kategori != null && !kategori.isEmpty()) {
            try { Complaint.Kategori kat = Complaint.Kategori.valueOf(kategori); complaints = complaints.stream().filter(c -> c.getKategori() == kat).toList(); } catch (Exception ignored) {}
        }
        if (status != null && !status.isEmpty()) {
            try { Complaint.Status stat = Complaint.Status.valueOf(status); complaints = complaints.stream().filter(c -> c.getStatus() == stat).toList(); } catch (Exception ignored) {}
        }
        model.addAttribute("complaints", complaints);
        return "admin-manage-complaints";
    }

    @PostMapping("/complaints/{id}/update-status")
    public String processStatusMutation(@PathVariable Long id, @RequestParam("status") String status) {
        try {
            Complaint complaint = complaintRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Target complaint structure missing"));
            String mappedStatus = mapIncomingStatus(status);
            Complaint.Status newStatus = Complaint.Status.valueOf(mappedStatus);
            Complaint.Status oldStatus = complaint.getStatus();
            complaint.setStatus(newStatus);
            LocalDateTime now = LocalDateTime.now();
            if (newStatus == Complaint.Status.PROSES && complaint.getProcessedAt() == null) {
                complaint.setProcessedAt(now);
            }
            if (newStatus == Complaint.Status.SELESAI) {
                complaint.setResolvedAt(now);
                if (complaint.getProcessedAt() == null) complaint.setProcessedAt(now);
            }
            complaintRepository.save(complaint);
            if (complaint.getUser() != null && !complaint.isIsAnonymous()) {
                try { notificationService.createNotification(Notification.Type.STATUS_CHANGE, "Status pengaduan \"" + complaint.getJudul() + "\" berubah dari " + oldStatus.getDisplayName() + " menjadi " + newStatus.getDisplayName(), complaint.getUser(), complaint); } catch (Exception ignored) {}
            }
        } catch (Exception e) { /* fallback */ }
        return "redirect:/admin/complaints/manage?mutationSuccess=true";
    }

    @PostMapping("/api/complaints/{id}/update-status")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Map<String, Object>> processStatusMutationAjax(
            @PathVariable Long id,
            @RequestParam(value = "status", required = false, defaultValue = "PENDING") String status) {
        try {
            Complaint complaint = complaintRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Pengaduan tidak ditemukan"));
            String mappedStatus = mapIncomingStatus(status);
            Complaint.Status newStatus = Complaint.Status.valueOf(mappedStatus);
            Complaint.Status oldStatus = complaint.getStatus();

            if (oldStatus == newStatus) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", mappedStatus,
                    "displayName", newStatus.getDisplayName(),
                    "message", "Status sudah " + newStatus.getDisplayName().toLowerCase()
                ));
            }

            complaint.setStatus(newStatus);
            LocalDateTime now = LocalDateTime.now();
            if (newStatus == Complaint.Status.PROSES && complaint.getProcessedAt() == null) {
                complaint.setProcessedAt(now);
            }
            if (newStatus == Complaint.Status.SELESAI) {
                complaint.setResolvedAt(now);
                if (complaint.getProcessedAt() == null) complaint.setProcessedAt(now);
                if (complaint.getUser() != null) {
                    try { userService.recalculateReputation(complaint.getUser().getId()); } catch (Exception ignored) {}
                }
            }
            complaintRepository.flush();

            // Notify complaint owner
            if (complaint.getUser() != null && !complaint.isIsAnonymous()) {
                try {
                    notificationService.createNotification(
                        Notification.Type.STATUS_CHANGE,
                        "Status pengaduan \"" + complaint.getJudul() + "\" berubah dari " + oldStatus.getDisplayName() + " menjadi " + newStatus.getDisplayName(),
                        complaint.getUser(),
                        complaint
                    );
                } catch (Exception ignored) {}
            }

            // Broadcast notification via WebSocket
            try {
                if (messagingTemplate != null) {
                    messagingTemplate.convertAndSend("/topic/notifications", Map.of("type", "STATUS_CHANGE"));
                }
            } catch (Exception ignored) {}

            return ResponseEntity.ok(Map.of(
                "success", true,
                "status", mappedStatus,
                "displayName", newStatus.getDisplayName()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false, "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/api/stats/daily")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDailyStats() {
        try {
            long total = complaintRepository.count();
            long pending = complaintRepository.countByStatus("PENDING");
            long processed = complaintRepository.countByStatus("PROSES");
            long resolved = complaintRepository.countByStatus("SELESAI");

            List<Object[]> kategoriData = complaintRepository.countGroupByKategori();
            Map<String, Long> kategoriMap = new LinkedHashMap<>();
            if (kategoriData != null) {
                for (Object[] row : kategoriData) {
                    kategoriMap.put(row[0] != null ? row[0].toString() : "UMUM",
                        row[1] != null ? ((Number) row[1]).longValue() : 0);
                }
            }

            List<Object[]> urgencyData = complaintRepository.countGroupByUrgency();
            Map<String, Long> urgencyMap = new LinkedHashMap<>();
            if (urgencyData != null) {
                for (Object[] row : urgencyData) {
                    urgencyMap.put(row[0] != null ? row[0].toString() : "RENDAH",
                        row[1] != null ? ((Number) row[1]).longValue() : 0);
                }
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("total", total);
            result.put("pending", pending);
            result.put("processed", processed);
            result.put("resolved", resolved);
            result.put("categories", kategoriMap);
            result.put("urgencies", urgencyMap);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/geo/complaints")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getGeoTaggedComplaints() {
        List<Complaint> geoTagged = complaintRepository.findByLatitudeIsNotNullAndLongitudeIsNotNull();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Complaint c : geoTagged) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", c.getId());
            item.put("judul", c.getJudul());
            item.put("latitude", c.getLatitude());
            item.put("longitude", c.getLongitude());
            item.put("status", c.getStatus() != null ? c.getStatus().name() : "PENDING");
            item.put("urgency", c.getUrgency() != null ? c.getUrgency().name() : "RENDAH");
            item.put("upvotesCount", c.getUpvotesCount());
            result.add(item);
        }
        return ResponseEntity.ok(result);
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

    @GetMapping("/broadcasts")
    public String viewBroadcasts(Model model) {
        model.addAttribute("broadcasts", broadcastService.getAllBroadcasts());
        return "admin-broadcasts";
    }

    @PostMapping("/broadcast/create")
    public String createBroadcast(@RequestParam("judul") String judul,
                                  @RequestParam("isiBroadcast") String isiBroadcast,
                                  Authentication authentication) {
        Pengguna admin = penggunaRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));
        Broadcast broadcast = new Broadcast(judul, isiBroadcast, admin);
        broadcastService.save(broadcast);
        return "redirect:/admin/broadcasts?success=Broadcast berhasil dikirim";
    }

    @PostMapping("/broadcast/{id}/update")
    public String updateBroadcast(@PathVariable Long id,
                                  @RequestParam("judul") String judul,
                                  @RequestParam("isiBroadcast") String isiBroadcast) {
        broadcastService.updateBroadcast(id, judul, isiBroadcast);
        return "redirect:/admin/broadcasts?success=Broadcast berhasil diperbarui";
    }

    @PostMapping("/broadcast/{id}/delete")
    public String deleteBroadcast(@PathVariable Long id) {
        broadcastService.deleteBroadcast(id);
        return "redirect:/admin/broadcasts?success=Broadcast berhasil dihapus";
    }

    @PostMapping("/broadcast/{id}/update-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateBroadcastAjax(@PathVariable Long id,
                                                                     @RequestBody Map<String, String> body) {
        try {
            broadcastService.updateBroadcast(id, body.get("judul"), body.get("isiBroadcast"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/broadcast/{id}/delete-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteBroadcastAjax(@PathVariable Long id) {
        try {
            broadcastService.deleteBroadcast(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/complaints/{id}")
    public String viewComplaintDetail(@PathVariable Long id, Model model) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));
        model.addAttribute("c", complaint);

        // Load comments via native query to avoid lazy-loading issues
        List<Object[]> rows = commentRepository.findCommentRawDataByComplaintId(id);
        List<Map<String, Object>> commentsList = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM HH:mm");
        for (Object[] row : rows) {
            Map<String, Object> cm = new LinkedHashMap<>();
            cm.put("id", row[0]);
            cm.put("isiKomentar", row[1]);
            if (row[2] instanceof java.sql.Timestamp ts) {
                cm.put("tanggalStr", ts.toLocalDateTime().format(dtf));
            } else if (row[2] instanceof LocalDateTime ldt) {
                cm.put("tanggalStr", ldt.format(dtf));
            } else {
                cm.put("tanggalStr", String.valueOf(row[2]));
            }
            Map<String, Object> u = new LinkedHashMap<>();
            u.put("nama", row[3] != null ? row[3] : "Warga");
            cm.put("user", u);
            commentsList.add(cm);
        }
        model.addAttribute("comments", commentsList);
        return "admin-complaint-detail";
    }

    @GetMapping("/warga/{id}")
    public String viewWargaDetail(@PathVariable Long id, Model model) {
        Pengguna user = penggunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warga tidak ditemukan"));
        model.addAttribute("u", user);
        model.addAttribute("complaints", complaintRepository.findByUserIdOrderByTanggalDesc(id));
        return "admin-user-detail";
    }

    @PostMapping("/warga/{id}/delete")
    public String deleteWarga(@PathVariable Long id) {
        penggunaRepository.deleteById(id);
        return "redirect:/admin/warga?success=Warga berhasil dihapus";
    }

    @PostMapping("/warga/{id}/edit")
    public String editWarga(@PathVariable Long id,
            @RequestParam("nama") String nama,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam(value = "password", required = false) String password) {
        try {
            Pengguna user = penggunaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Warga tidak ditemukan"));
            user.setNama(nama);
            user.setEmail(email);
            user.setRole(role);
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            penggunaRepository.save(user);
            return "redirect:/admin/warga/" + id + "?edited=true";
        } catch (Exception e) {
            return "redirect:/admin/warga/" + id + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/warga/{id}/notify")
    public String sendNotificationToWarga(@PathVariable Long id,
            @RequestParam String judul, @RequestParam String pesan) {
        Pengguna user = penggunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warga tidak ditemukan"));
        notificationService.createNotification(
            Notification.Type.SYSTEM, pesan, user, null);
        return "redirect:/admin/warga/" + id + "?notified=true";
    }

    // ─── Poll Management ──────────────────────────────────────────────
    @GetMapping("/polls")
    public String viewPolls(Model model) {
        try {
            model.addAttribute("polls", pollRepository.findAllByOrderByCreatedAtDesc());
        } catch (Exception e) {
            model.addAttribute("polls", new ArrayList<>());
        }
        return "admin-polls";
    }

    @PostMapping("/polls/create")
    public String createPoll(@RequestParam("question") String question,
                             Authentication authentication) {
        Pengguna admin = penggunaRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));
        Poll poll = new Poll(question, admin);
        try {
            pollRepository.save(poll);
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat polling: " + e.getMessage());
        }
        return "redirect:/admin/polls?success=Polling berhasil dibuat";
    }

    @PostMapping("/polls/{id}/close")
    public String closePoll(@PathVariable Long id) {
        try {
            Poll poll = pollRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Polling tidak ditemukan"));
            poll.setIsActive(false);
            poll.setClosedAt(LocalDateTime.now());
            pollRepository.save(poll);
        } catch (Exception e) {
            return "redirect:/admin/polls?error=Gagal menutup polling";
        }
        return "redirect:/admin/polls?success=Polling ditutup";
    }

    // ─── Map View ─────────────────────────────────────────────────────
    @GetMapping("/map")
    public String viewMap() {
        return "admin-map";
    }

    @GetMapping("/stats/kategori")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getKategoriStats() {
        try {
            List<Object[]> data = complaintRepository.countGroupByKategori();
            List<Map<String, Object>> result = new ArrayList<>();
            if (data != null) {
                for (Object[] row : data) {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("kategori", row[0] != null ? row[0].toString() : "UMUM");
                    item.put("count", row[1] != null ? ((Number) row[1]).longValue() : 0);
                    result.add(item);
                }
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/stats/urgensi")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getUrgensiStats() {
        try {
            List<Object[]> data = complaintRepository.countGroupByUrgency();
            List<Map<String, Object>> result = new ArrayList<>();
            if (data != null) {
                for (Object[] row : data) {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("urgensi", row[0] != null ? row[0].toString() : "RENDAH");
                    item.put("count", row[1] != null ? ((Number) row[1]).longValue() : 0);
                    result.add(item);
                }
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/complaints/export/csv")
    public ResponseEntity<byte[]> exportComplaintsCsv() {
        List<Complaint> complaints = complaintRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Judul,Deskripsi,Kategori,Urgensi,Status,Tanggal,Pelapor,Latitude,Longitude,Upvotes\n");
        for (Complaint c : complaints) {
            sb.append(c.getId()).append(",");
            sb.append(escapeCsv(c.getJudul())).append(",");
            sb.append(escapeCsv(c.getIsiPengaduan())).append(",");
            sb.append(c.getKategori() != null ? c.getKategori().name() : "UMUM").append(",");
            sb.append(c.getUrgency() != null ? c.getUrgency().name() : "RENDAH").append(",");
            sb.append(c.getStatus() != null ? c.getStatus().name() : "PENDING").append(",");
            sb.append(c.getTanggal() != null ? c.getTanggal().toString() : "").append(",");
            sb.append(c.isIsAnonymous() ? "Anonim" : (c.getUser() != null ? escapeCsv(c.getUser().getNama()) : "Unknown")).append(",");
            sb.append(c.getLatitude() != null ? c.getLatitude() : "").append(",");
            sb.append(c.getLongitude() != null ? c.getLongitude() : "").append(",");
            sb.append(c.getUpvotesCount()).append("\n");
        }
        byte[] bytes = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "complaints.csv");
        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

}
