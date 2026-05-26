package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateBroadcastRequest;
import com.example.smartcommunity.model.Broadcast;
import com.example.smartcommunity.model.Notification;
import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.repository.BroadcastRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.service.BroadcastService;
import com.example.smartcommunity.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class BroadcastServiceImpl implements BroadcastService {

    private final BroadcastRepository broadcastRepository;
    private final PenggunaRepository penggunaRepository;
    private final NotificationService notificationService;

    public BroadcastServiceImpl(BroadcastRepository broadcastRepository,
                                PenggunaRepository penggunaRepository,
                                NotificationService notificationService) {
        this.broadcastRepository = broadcastRepository;
        this.penggunaRepository = penggunaRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Broadcast createBroadcast(CreateBroadcastRequest request) {
        Pengguna admin = penggunaRepository.findById(request.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));

        Broadcast broadcast = new Broadcast(request.getJudul(), request.getIsiBroadcast(), admin);
        Broadcast saved = broadcastRepository.save(broadcast);

        // Send notification to all citizens
        List<Pengguna> citizens = penggunaRepository.findByRole("CITIZEN");
        for (Pengguna citizen : citizens) {
            notificationService.createNotification(
                    Notification.Type.SYSTEM,
                    "Pengumuman baru: " + request.getJudul(),
                    citizen,
                    null
            );
        }

        return saved;
    }

    @Override
    public List<Broadcast> getAllBroadcasts() {
        try {
            List<Broadcast> result = broadcastRepository.findAllByOrderByTanggalDesc();
            if (result == null) return Collections.emptyList();
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Broadcast> findAllActive() {
        return getAllBroadcasts();
    }

        @Override
    public Broadcast updateBroadcast(Long id, String judul, String isiBroadcast) {
        Broadcast broadcast = broadcastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Broadcast tidak ditemukan"));
        broadcast.setJudul(judul);
        broadcast.setIsiBroadcast(isiBroadcast);
        return broadcastRepository.save(broadcast);
    }

    @Override
    public void deleteBroadcast(Long id) {
        if (!broadcastRepository.existsById(id)) {
            throw new RuntimeException("Broadcast tidak ditemukan");
        }
        broadcastRepository.deleteById(id);
    }

    @Override
    public Broadcast findById(Long id) {
        return broadcastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Broadcast tidak ditemukan"));
    }

    @Override
    public Broadcast save(Broadcast broadcast) {
        if (broadcast.getTanggal() == null) {
            broadcast.setTanggal(LocalDateTime.now());
        }
        if (broadcast.getAdmin() == null) {
            List<Pengguna> admins = penggunaRepository.findAll().stream()
                    .filter(u -> "ADMIN".equals(u.getRole()))
                    .toList();
            if (!admins.isEmpty()) {
                broadcast.setAdmin(admins.get(0));
            }
        }
        return broadcastRepository.save(broadcast);
    }
}
