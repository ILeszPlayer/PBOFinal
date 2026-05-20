package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.RegisterCitizenRequest;
import com.example.smartcommunity.model.Citizen;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.model.UserProfile;
import com.example.smartcommunity.repository.UserProfileRepository;
import com.example.smartcommunity.repository.UserRepository;
import com.example.smartcommunity.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserProfileRepository userProfileRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerCitizen(RegisterCitizenRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email sudah terdaftar");
        }

        Citizen citizen = new Citizen(request.getNama(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()));
        citizen = userRepository.save(citizen);

        LocalDate tglLahir = null;
        if (request.getTanggalLahir() != null && !request.getTanggalLahir().isEmpty()) {
            tglLahir = LocalDate.parse(request.getTanggalLahir());
        }

        UserProfile profile = new UserProfile(citizen, request.getNik(),
                request.getAlamat(), request.getNomorTelepon(), tglLahir);
        userProfileRepository.save(profile);

        return citizen;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    @Override
    public User awardPoints(Long userId, int points) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        user.setReputationPoints(user.getReputationPoints() + points);
        return userRepository.save(user);
    }

    @Override
    public User recalculateReputation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        long approvedCount = user.getComplaints().stream()
                .filter(c -> c.getStatus() == Complaint.Status.SELESAI)
                .count();
        int totalUpvotes = user.getComplaints().stream()
                .mapToInt(Complaint::getUpvotesCount)
                .sum();

        int newPoints = ((int) approvedCount * 20) + (totalUpvotes * 5);
        user.setReputationPoints(newPoints);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }
}
