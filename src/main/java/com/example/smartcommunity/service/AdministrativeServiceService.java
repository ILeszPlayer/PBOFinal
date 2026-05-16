package com.example.smartcommunity.service;

import com.example.smartcommunity.model.AdministrativeService;

import java.util.List;

public interface AdministrativeServiceService {

    List<AdministrativeService> getAllServices();

    AdministrativeService saveService(AdministrativeService service);
}