package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.exception.ResourceNotFoundException;
import com.example.smartcommunity.model.AdministrativeService;
import com.example.smartcommunity.repository.AdministrativeServiceRepository;
import com.example.smartcommunity.service.AdministrativeServiceService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdministrativeServiceServiceImpl implements AdministrativeServiceService {
    private final AdministrativeServiceRepository repository;

    public AdministrativeServiceServiceImpl(AdministrativeServiceRepository repository) {
        this.repository = repository;
    }

    public List<AdministrativeService> findAll() { return repository.findAll(); }
    public AdministrativeService findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Layanan administrasi tidak ditemukan"));
    }
    public AdministrativeService create(AdministrativeService service) { return repository.save(service); }
    public AdministrativeService update(Long id, AdministrativeService input) {
        AdministrativeService service = findById(id);
        service.setNamaLayanan(input.getNamaLayanan());
        service.setDeskripsi(input.getDeskripsi());
        service.setPersyaratan(input.getPersyaratan());
        service.setEstimasiWaktu(input.getEstimasiWaktu());
        return repository.save(service);
    }
    public void delete(Long id) { repository.delete(findById(id)); }
}
