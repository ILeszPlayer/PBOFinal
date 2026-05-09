package com.example.smartcommunity.service;

import com.example.smartcommunity.model.AdministrativeService;
import java.util.List;

public interface AdministrativeServiceService {
    List<AdministrativeService> findAll();
    AdministrativeService findById(Long id);
    AdministrativeService create(AdministrativeService service);
    AdministrativeService update(Long id, AdministrativeService service);
    void delete(Long id);
}
