package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateBroadcastRequest;
import com.example.smartcommunity.model.Broadcast;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.repository.BroadcastRepository;
import com.example.smartcommunity.repository.UserRepository;
import com.example.smartcommunity.service.BroadcastService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BroadcastServiceImpl implements BroadcastService {

    private final BroadcastRepository broadcastRepository;
    private final UserRepository userRepository;

    public BroadcastServiceImpl(BroadcastRepository broadcastRepository,
                                UserRepository userRepository) {
        this.broadcastRepository = broadcastRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Broadcast createBroadcast(CreateBroadcastRequest request) {
        User admin = userRepository.findById(request.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));

        Broadcast broadcast = new Broadcast(request.getJudul(), request.getIsiBroadcast(), admin);
        return broadcastRepository.save(broadcast);
    }

    @Override
    public List<Broadcast> getAllBroadcasts() {
        return broadcastRepository.findAllByOrderByTanggalDesc();
    }
}
