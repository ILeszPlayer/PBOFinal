package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.UserRepository;
import com.example.smartcommunity.service.ComplaintService;
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
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Complaint createComplaint(CreateComplaintRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Complaint.Kategori kategori = Complaint.Kategori.valueOf(request.getKategori().toUpperCase());

        Complaint complaint = new Complaint();
        complaint.setJudul(request.getJudul());
        complaint.setIsiPengaduan(request.getIsiPengaduan());
        complaint.setKategori(kategori);
        complaint.setUser(user);
        complaint.setTanggal(LocalDateTime.now());
        complaint.setStatus(Complaint.Status.MENUNGGU);

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

        return complaintRepository.save(complaint);
    }

    @Override
    public Complaint updateStatus(Long complaintId, Complaint.Status status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));
        complaint.setStatus(status);
        return complaintRepository.save(complaint);
    }

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByTanggalDesc();
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
}
