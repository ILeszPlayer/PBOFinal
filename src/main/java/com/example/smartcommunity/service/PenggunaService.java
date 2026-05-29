package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.RegisterCitizenRequest;
import com.example.smartcommunity.model.Pengguna;

import java.util.List;

public interface PenggunaService {
    Pengguna registerCitizen(RegisterCitizenRequest request);
    Pengguna findById(Long id);
    Pengguna findByEmail(String email);
    Pengguna awardPoints(Long userId, int points);
    Pengguna recalculateReputation(Long userId);
    void save(Pengguna user);
    List<Pengguna> getAllUsers();
    long countUsers();
    List<Pengguna> findAllCitizens();
}
