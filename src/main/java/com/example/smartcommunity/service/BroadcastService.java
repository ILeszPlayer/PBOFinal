package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.CreateBroadcastRequest;
import com.example.smartcommunity.model.Broadcast;

import java.util.List;

public interface BroadcastService {
    Broadcast createBroadcast(CreateBroadcastRequest request);
    List<Broadcast> getAllBroadcasts();
    List<Broadcast> findAllActive();
    Broadcast save(Broadcast broadcast);
    Broadcast updateBroadcast(Long id, String judul, String isiBroadcast);
    void deleteBroadcast(Long id);
    Broadcast findById(Long id);
}
