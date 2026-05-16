package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.model.AdministrativeService;
import com.example.smartcommunity.repository.AdministrativeServiceRepository;
import com.example.smartcommunity.service.AdministrativeServiceService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministrativeServiceServiceImpl
        implements AdministrativeServiceService {

    private final AdministrativeServiceRepository repository;

    public AdministrativeServiceServiceImpl(
            AdministrativeServiceRepository repository) {

        this.repository = repository;
    }

    @Override
    public List<AdministrativeService> getAllServices() {
        return repository.findAll();
    }

    @Override
    public AdministrativeService saveService(
            AdministrativeService service) {

        return repository.save(service);
    }
}