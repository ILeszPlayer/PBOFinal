package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateBroadcastRequest;
import com.example.smartcommunity.model.Broadcast;
import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.repository.BroadcastRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.service.BroadcastService;
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

    public BroadcastServiceImpl(BroadcastRepository broadcastRepository,
                                PenggunaRepository penggunaRepository) {
        this.broadcastRepository = broadcastRepository;
        this.penggunaRepository = penggunaRepository;
    }

    @Override
    public Broadcast createBroadcast(CreateBroadcastRequest request) {
        Pengguna admin = penggunaRepository.findById(request.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));

        Broadcast broadcast = new Broadcast(request.getJudul(), request.getIsiBroadcast(), admin);
        return broadcastRepository.save(broadcast);
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
