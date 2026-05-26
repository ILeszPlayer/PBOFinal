package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Notification;
import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.service.ComplaintService;
import com.example.smartcommunity.service.NotificationService;
import com.example.smartcommunity.service.PenggunaService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final PenggunaRepository penggunaRepository;
    private final PenggunaService penggunaService;
    private final NotificationService notificationService;

    @Value("${app.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                PenggunaRepository penggunaRepository,
                                PenggunaService penggunaService,
                                NotificationService notificationService) {
        this.complaintRepository = complaintRepository;
        this.penggunaRepository = penggunaRepository;
        this.penggunaService = penggunaService;
        this.notificationService = notificationService;
    }

    @Override
    public Complaint createComplaint(CreateComplaintRequest request) {
        Pengguna user = penggunaRepository.findById(request.getUserId())
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
        penggunaService.awardPoints(request.getUserId(), 10);

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
            Pengguna complainant = complaint.getUser();
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
            penggunaService.recalculateReputation(complainant.getId());
        } catch (Exception ignored) {}

        return complaint;
    }

    @Override
    public Complaint upvote(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));
        complaint.setUpvotesCount(complaint.getUpvotesCount() + 1);
        return complaintRepository.save(complaint);
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
        if (kategori == null) return getAllComplaints();
        return complaintRepository.findByKategori(kategori);
    }

    @Override
    public List<Complaint> getComplaintsByStatus(Complaint.Status status) {
        if (status == null) return getAllComplaints();
        return complaintRepository.findByStatus(status);
    }

    @Override
    public List<Complaint> getComplaintsByUrgency(Complaint.Urgency urgency) {
        if (urgency == null) return getAllComplaints();
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
        if (status == null) return 0;
        return complaintRepository.countByStatus(status);
    }

    @Override
    public long countAll() {
        return complaintRepository.count();
    }

    @Override
    public List<Object[]> countByKategori() {
        try {
            return complaintRepository.countGroupByKategori();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object[]> countByUrgency() {
        try {
            return complaintRepository.countGroupByUrgency();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object[]> countResolvedByMonth() {
        try {
            return complaintRepository.countResolvedByMonth();
        } catch (Exception e) {
            return new ArrayList<>();
        }
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
        java.util.Map<String, Complaint.Kategori> keywordCategories = new java.util.LinkedHashMap<>();
        keywordCategories.put("jalan", Complaint.Kategori.INFRASTRUKTUR);
        keywordCategories.put("lubang", Complaint.Kategori.INFRASTRUKTUR);
        keywordCategories.put("trotoar", Complaint.Kategori.INFRASTRUKTUR);
        keywordCategories.put("jembatan", Complaint.Kategori.INFRASTRUKTUR);
        keywordCategories.put("aspal", Complaint.Kategori.INFRASTRUKTUR);
        keywordCategories.put("lampu", Complaint.Kategori.INFRASTRUKTUR);
        keywordCategories.put("selokan", Complaint.Kategori.INFRASTRUKTUR);
        keywordCategories.put("sampah", Complaint.Kategori.LINGKUNGAN);
        keywordCategories.put("bau", Complaint.Kategori.LINGKUNGAN);
        keywordCategories.put("pohon", Complaint.Kategori.LINGKUNGAN);
        keywordCategories.put("taman", Complaint.Kategori.LINGKUNGAN);
        keywordCategories.put("polusi", Complaint.Kategori.LINGKUNGAN);
        keywordCategories.put("got", Complaint.Kategori.LINGKUNGAN);
        keywordCategories.put("keamanan", Complaint.Kategori.KEAMANAN);
        keywordCategories.put("kriminal", Complaint.Kategori.KEAMANAN);
        keywordCategories.put("maling", Complaint.Kategori.KEAMANAN);
        keywordCategories.put("begal", Complaint.Kategori.KEAMANAN);
        keywordCategories.put("tawuran", Complaint.Kategori.KEAMANAN);
        keywordCategories.put("kesehatan", Complaint.Kategori.KESEHATAN);
        keywordCategories.put("rumah sakit", Complaint.Kategori.KESEHATAN);
        keywordCategories.put("puskesmas", Complaint.Kategori.KESEHATAN);
        keywordCategories.put("obat", Complaint.Kategori.KESEHATAN);
        keywordCategories.put("pendidikan", Complaint.Kategori.PENDIDIKAN);
        keywordCategories.put("sekolah", Complaint.Kategori.PENDIDIKAN);
        keywordCategories.put("guru", Complaint.Kategori.PENDIDIKAN);
        keywordCategories.put("sosial", Complaint.Kategori.SOSIAL);
        keywordCategories.put("bansos", Complaint.Kategori.SOSIAL);
        keywordCategories.put("bantuan", Complaint.Kategori.SOSIAL);
        keywordCategories.put("warga", Complaint.Kategori.SOSIAL);

        for (java.util.Map.Entry<String, Complaint.Kategori> entry : keywordCategories.entrySet()) {
            if (text.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return Complaint.Kategori.UMUM;
    }

    private Complaint.Urgency classifyUrgency(String text) {
        java.util.Map<String, Integer> urgencyKeywords = new java.util.LinkedHashMap<>();
        urgencyKeywords.put("darurat", 3);
        urgencyKeywords.put("banjir", 3);
        urgencyKeywords.put("kecelakaan", 3);
        urgencyKeywords.put("kebakaran", 3);
        urgencyKeywords.put("longsor", 3);
        urgencyKeywords.put("ambruk", 3);
        urgencyKeywords.put("mendesak", 3);
        urgencyKeywords.put("kritis", 3);
        urgencyKeywords.put("segara", 2);
        urgencyKeywords.put("penting", 2);
        urgencyKeywords.put("butuh", 2);
        urgencyKeywords.put("tolong", 2);
        urgencyKeywords.put("cepat", 2);

        int maxScore = 0;
        for (java.util.Map.Entry<String, Integer> entry : urgencyKeywords.entrySet()) {
            if (text.contains(entry.getKey())) {
                maxScore = Math.max(maxScore, entry.getValue());
            }
        }
        if (maxScore >= 3) return Complaint.Urgency.TINGGI;
        if (maxScore >= 2) return Complaint.Urgency.SEDANG;
        return Complaint.Urgency.RENDAH;
    }
}
