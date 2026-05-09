package com.example.smartcommunity.controller;

import com.example.smartcommunity.model.AdministrativeService;
import com.example.smartcommunity.service.AdministrativeServiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/services")
public class AdministrativeServiceController {
    private final AdministrativeServiceService service;
    public AdministrativeServiceController(AdministrativeServiceService service) { this.service = service; }

    @GetMapping
    public List<AdministrativeService> getAllServices() { return service.findAll(); }

    @GetMapping("/{id}")
    public AdministrativeService getServiceById(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    public AdministrativeService createService(@Valid @RequestBody AdministrativeService input) { return service.create(input); }

    @PutMapping("/{id}")
    public AdministrativeService updateService(@PathVariable Long id, @Valid @RequestBody AdministrativeService input) { return service.update(id, input); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
