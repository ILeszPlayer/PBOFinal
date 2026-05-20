package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Notification;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.NotificationRepository;
import com.example.smartcommunity.repository.UserRepository;
import com.example.smartcommunity.service.ComplaintService;
import com.example.smartcommunity.service.NotificationService;
import com.example.smartcommunity.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Value("${app.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    private static final Map<String, Complaint.Kategori> KEYWORD_CATEGORIES = new LinkedHashMap<>();
    static {
        KEYWORD_CATEGORIES.put("jalan", Complaint.Kategori.INFRASTRUKTUR);
        KEYWORD_CATEGORIES.put("lubang", Complaint.Kategori.INFRASTRUKTUR);
        KEYWORD_CATEGORIES.put("trotoar", Complaint.Kategori.INFRASTRUKTUR);
        KEYWORD_CATEGORIES.put("jembatan", Complaint.Kategori.INFRASTRUKTUR);
        KEYWORD_CATEGORIES.put("aspal", Complaint.Kategori.INFRASTRUKTUR);
        KEYWORD_CATEGORIES.put("lampu", Complaint.Kategori.INFRASTRUKTUR);
        KEYWORD_CATEGORIES.put("selokan", Complaint.Kategori.INFRASTRUKTUR);
        KEYWORD_CATEGORIES.put("sampah", Complaint.Kategori.LINGKUNGAN);
        KEYWORD_CATEGORIES.put("bau", Complaint.Kategori.LINGKUNGAN);
        KEYWORD_CATEGORIES.put("pohon", Complaint.Kategori.LINGKUNGAN);
        KEYWORD_CATEGORIES.put("taman", Complaint.Kategori.LINGKUNGAN);
        KEYWORD_CATEGORIES.put("polusi", Complaint.Kategori.LINGKUNGAN);
        KEYWORD_CATEGORIES.put("got", Complaint.Kategori.LINGKUNGAN);
        KEYWORD_CATEGORIES.put("keamanan", Complaint.Kategori.KEAMANAN);
        KEYWORD_CATEGORIES.put("kriminal", Complaint.Kategori.KEAMANAN);
        KEYWORD_CATEGORIES.put("maling", Complaint.Kategori.KEAMANAN);
        KEYWORD_CATEGORIES.put("begal", Complaint.Kategori.KEAMANAN);
        KEYWORD_CATEGORIES.put("tawuran", Complaint.Kategori.KEAMANAN);
        KEYWORD_CATEGORIES.put("kesehatan", Complaint.Kategori.KESEHATAN);
        KEYWORD_CATEGORIES.put("rumah sakit", Complaint.Kategori.KESEHATAN);
        KEYWORD_CATEGORIES.put("puskesmas", Complaint.Kategori.KESEHATAN);
        KEYWORD_CATEGORIES.put("obat", Complaint.Kategori.KESEHATAN);
        KEYWORD_CATEGORIES.put("pendidikan", Complaint.Kategori.PENDIDIKAN);
        KEYWORD_CATEGORIES.put("sekolah", Complaint.Kategori.PENDIDIKAN);
        KEYWORD_CATEGORIES.put("guru", Complaint.Kategori.PENDIDIKAN);
        KEYWORD_CATEGORIES.put("sosial", Complaint.Kategori.SOSIAL);
        KEYWORD_CATEGORIES.put("bansos", Complaint.Kategori.SOSIAL);
        KEYWORD_CATEGORIES.put("bantuan", Complaint.Kategori.SOSIAL);
        KEYWORD_CATEGORIES.put("warga", Complaint.Kategori.SOSIAL);
    }

    private static final Map<String, Integer> URGENCY_KEYWORDS = new LinkedHashMap<>();
    static {
        URGENCY_KEYWORDS.put("darurat", 3);
        URGENCY_KEYWORDS.put("banjir", 3);
        URGENCY_KEYWORDS.put("kecelakaan", 3);
        URGENCY_KEYWORDS.put("kebakaran", 3);
        URGENCY_KEYWORDS.put("longsor", 3);
        URGENCY_KEYWORDS.put("ambruk", 3);
        URGENCY_KEYWORDS.put("mendesak", 3);
        URGENCY_KEYWORDS.put("kritis", 3);
        URGENCY_KEYWORDS.put("segara", 2);
        URGENCY_KEYWORDS.put("penting", 2);
        URGENCY_KEYWORDS.put("butuh", 2);
        URGENCY_KEYWORDS.put("tolong", 2);
        URGENCY_KEYWORDS.put("cepat", 2);
    }

    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                UserRepository userRepository,
                                UserService userService,
                                NotificationService notificationService) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public Complaint createComplaint(CreateComplaintRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        String isi = request.getIsiPengaduan().toLowerCase();
        String judul = request.getJudul().toLowerCase();
        String combined = isi + " " + judul;

        Complaint.Kategori kategori;
        if (request.getKategori() != null && !request.getKategori().isEmpty()) {
            kategori = Complaint.Kategori.valueOf(request.getKategori().toUpperCase());
        } else {
            kategori = classifyCategory(combined);
        }

        Complaint.Urgency urgency = classifyUrgency(combined);

        Complaint complaint = new Complaint();
        complaint.setJudul(request.getJudul());
        complaint.setIsiPengaduan(request.getIsiPengaduan());
        complaint.setKategori(kategori);
        complaint.setUrgency(urgency);
        complaint.setUser(user);
        complaint.setTanggal(LocalDateTime.now());
        complaint.setStatus(Complaint.Status.PENDING);
        complaint.setIsAnonymous(request.isIsAnonymous());
        complaint.setLatitude(request.getLatitude());
        complaint.setLongitude(request.getLongitude());
        complaint.setLokasiNama(request.getLokasiNama());

        MultipartFile file = request.getBuktiFoto();
        if (file != null && !file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                complaint.setBuktiFoto("/uploads/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Gagal mengupload file: " + e.getMessage());
            }
        }

        complaint = complaintRepository.save(complaint);
        userService.awardPoints(request.getUserId(), 10);

        try {
            notificationService.createNotification(
                Notification.Type.NEW_COMPLAINT,
                "Pengaduan baru: \"" + complaint.getJudul() + "\" telah dibuat dan menunggu diproses.",
                user,
                complaint
            );
        } catch (Exception ignored) {}

        return complaint;
    }

    @Override
    public Complaint updateStatus(Long complaintId, Complaint.Status status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));

        Complaint.Status previousStatus = complaint.getStatus();
        complaint.setStatus(status);

        LocalDateTime now = LocalDateTime.now();
        if (status == Complaint.Status.PROSES && previousStatus == Complaint.Status.PENDING) {
            complaint.setProcessedAt(now);
        }
        if (status == Complaint.Status.SELESAI) {
            complaint.setResolvedAt(now);
            if (complaint.getProcessedAt() == null) {
                complaint.setProcessedAt(now);
            }
        }

        complaint = complaintRepository.save(complaint);

        try {
            User complainant = complaint.getUser();
            String statusMsg = switch (status) {
                case PROSES -> "Pengaduan \"" + complaint.getJudul() + "\" sedang diproses.";
                case SELESAI -> "Pengaduan \"" + complaint.getJudul() + "\" telah selesai ditangani. Terima kasih!";
                default -> "Status pengaduan \"" + complaint.getJudul() + "\" telah diperbarui.";
            };
            notificationService.createNotification(
                Notification.Type.STATUS_CHANGE,
                statusMsg,
                complainant,
                complaint
            );
            userService.recalculateReputation(complainant.getId());
        } catch (Exception ignored) {}

        return complaint;
    }

    @Override
    public Complaint upvote(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));
        complaint.setUpvotesCount(complaint.getUpvotesCount() + 1);
        complaint = complaintRepository.save(complaint);
        return complaint;
    }

    @Override
    public void deleteComplaint(Long complaintId) {
        if (!complaintRepository.existsById(complaintId)) {
            throw new RuntimeException("Pengaduan tidak ditemukan");
        }
        complaintRepository.deleteById(complaintId);
    }

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByTanggalDesc();
    }

    @Override
    public List<Complaint> searchComplaints(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllComplaints();
        }
        return complaintRepository.findByJudulContainingIgnoreCaseOrIsiPengaduanContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public List<Complaint> getComplaintsByUser(Long userId) {
        return complaintRepository.findByUserIdOrderByTanggalDesc(userId);
    }

    @Override
    public List<Complaint> getComplaintsByCategory(Complaint.Kategori kategori) {
        return complaintRepository.findByKategori(kategori);
    }

    @Override
    public List<Complaint> getComplaintsByStatus(Complaint.Status status) {
        return complaintRepository.findByStatus(status);
    }

    @Override
    public List<Complaint> getComplaintsByUrgency(Complaint.Urgency urgency) {
        return complaintRepository.findByUrgency(urgency);
    }

    @Override
    public List<Complaint> getComplaintsSortedByUpvotes() {
        return complaintRepository.findAllByOrderByUpvotesCountDesc();
    }

    @Override
    public List<Complaint> getGeoTaggedComplaints() {
        return complaintRepository.findByLatitudeIsNotNullAndLongitudeIsNotNull();
    }

    @Override
    public Complaint findById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));
    }

    @Override
    public long countByStatus(Complaint.Status status) {
        return complaintRepository.countByStatus(status);
    }

    @Override
    public long countAll() {
        return complaintRepository.count();
    }

    @Override
    public List<Object[]> countByKategori() {
        return complaintRepository.countGroupByKategori();
    }

    @Override
    public List<Object[]> countByUrgency() {
        return complaintRepository.countGroupByUrgency();
    }

    @Override
    public List<Object[]> countResolvedByMonth() {
        return complaintRepository.countResolvedByMonth();
    }

    @Override
    public double getAverageSlaHours() {
        List<Complaint> resolved = complaintRepository.findByStatus(Complaint.Status.SELESAI);
        if (resolved.isEmpty()) return 0;
        return resolved.stream()
                .filter(c -> c.getProcessedAt() != null)
                .mapToLong(Complaint::getSlaHours)
                .average()
                .orElse(0);
    }

    private Complaint.Kategori classifyCategory(String text) {
        for (Map.Entry<String, Complaint.Kategori> entry : KEYWORD_CATEGORIES.entrySet()) {
            if (text.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return Complaint.Kategori.UMUM;
    }

    private Complaint.Urgency classifyUrgency(String text) {
        int maxScore = 0;
        for (Map.Entry<String, Integer> entry : URGENCY_KEYWORDS.entrySet()) {
            if (text.contains(entry.getKey())) {
                maxScore = Math.max(maxScore, entry.getValue());
            }
        }
        if (maxScore >= 3) return Complaint.Urgency.TINGGI;
        if (maxScore >= 2) return Complaint.Urgency.SEDANG;
        return Complaint.Urgency.RENDAH;
    }
}
