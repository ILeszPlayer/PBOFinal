package com.example.smartcommunity.controller;

import com.example.smartcommunity.model.AdministrativeService;
import com.example.smartcommunity.service.AdministrativeServiceService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class AdministrativeServiceController {

    private final AdministrativeServiceService service;

    public AdministrativeServiceController(
            AdministrativeServiceService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdministrativeService> getAllServices() {
        return service.getAllServices();
    }
}