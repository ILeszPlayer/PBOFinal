package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.RegisterCitizenRequest;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.model.UserProfile;
import com.example.smartcommunity.model.Warga;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.repository.UserProfileRepository;
import com.example.smartcommunity.service.PenggunaService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PenggunaServiceImpl implements PenggunaService {

    private final PenggunaRepository penggunaRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public PenggunaServiceImpl(PenggunaRepository penggunaRepository,
                               UserProfileRepository userProfileRepository,
                               PasswordEncoder passwordEncoder) {
        this.penggunaRepository = penggunaRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Pengguna registerCitizen(RegisterCitizenRequest request) {
        if (penggunaRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email sudah terdaftar");
        }

        Warga warga = new Warga(request.getNama(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()));
        warga = penggunaRepository.save(warga);

        LocalDate tglLahir = null;
        if (request.getTanggalLahir() != null && !request.getTanggalLahir().isEmpty()) {
            tglLahir = LocalDate.parse(request.getTanggalLahir());
        }

        UserProfile profile = new UserProfile(warga, request.getNik(),
                request.getAlamat(), request.getNomorTelepon(), tglLahir);
        userProfileRepository.save(profile);

        return warga;
    }

    @Override
    public Pengguna findById(Long id) {
        return penggunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    @Override
    public Pengguna findByEmail(String email) {
        return penggunaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    @Override
    public Pengguna awardPoints(Long userId, int points) {
        Pengguna user = penggunaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        user.setReputationPoints(user.getReputationPoints() + points);
        return penggunaRepository.save(user);
    }

    @Override
    public Pengguna recalculateReputation(Long userId) {
        Pengguna user = penggunaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        long approvedCount = user.getComplaints().stream()
                .filter(c -> c.getStatus() == Complaint.Status.SELESAI)
                .count();
        int totalUpvotes = user.getComplaints().stream()
                .mapToInt(Complaint::getUpvotesCount)
                .sum();

        int newPoints = ((int) approvedCount * 20) + (totalUpvotes * 5);
        user.setReputationPoints(newPoints);
        return penggunaRepository.save(user);
    }

    @Override
    public List<Pengguna> getAllUsers() {
        return penggunaRepository.findAll();
    }

    @Override
    public long countUsers() {
        return penggunaRepository.count();
    }

    @Override
    public void save(Pengguna user) {
        penggunaRepository.save(user);
    }

    @Override
    public List<Pengguna> findAllCitizens() {
        return penggunaRepository.findAll().stream()
                .filter(u -> "CITIZEN".equals(u.getRole()))
                .collect(Collectors.toList());
    }
}
