package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.RegisterCitizenRequest;
import com.example.smartcommunity.model.User;

import java.util.List;

public interface UserService {
    User registerCitizen(RegisterCitizenRequest request);
    User findById(Long id);
    User findByEmail(String email);
    List<User> getAllUsers();
    long countUsers();
}
