package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.CreateBroadcastRequest;
import com.example.smartcommunity.model.Broadcast;

import java.util.List;

public interface BroadcastService {
    Broadcast createBroadcast(CreateBroadcastRequest request);
    List<Broadcast> getAllBroadcasts();
}
