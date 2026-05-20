package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.RegisterCitizenRequest;
import com.example.smartcommunity.model.User;

import java.util.List;

public interface UserService {
    User registerCitizen(RegisterCitizenRequest request);
    User findById(Long id);
    User findByEmail(String email);
    User awardPoints(Long userId, int points);
    User recalculateReputation(Long userId);
    List<User> getAllUsers();
    long countUsers();
}
