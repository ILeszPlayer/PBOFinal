package com.example.smartcommunity.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrative_services")
public class AdministrativeService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_service")
    private Long idService;

    private String namaLayanan;

    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    @Column(columnDefinition = "TEXT")
    private String persyaratan;

    private String estimasiWaktu;

    @OneToMany(mappedBy = "administrativeService")
    private List<ServiceRequest> serviceRequests
            = new ArrayList<>();

    // Getter Setter

    public Long getIdService() {
        return idService;
    }

    public void setIdService(Long idService) {
        this.idService = idService;
    }

    public String getNamaLayanan() {
        return namaLayanan;
    }

    public void setNamaLayanan(String namaLayanan) {
        this.namaLayanan = namaLayanan;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getPersyaratan() {
        return persyaratan;
    }

    public void setPersyaratan(String persyaratan) {
        this.persyaratan = persyaratan;
    }

    public String getEstimasiWaktu() {
        return estimasiWaktu;
    }

    public void setEstimasiWaktu(String estimasiWaktu) {
        this.estimasiWaktu = estimasiWaktu;
    }

    public List<ServiceRequest> getServiceRequests() {
        return serviceRequests;
    }

    public void setServiceRequests(
            List<ServiceRequest> serviceRequests) {

        this.serviceRequests = serviceRequests;
    }
}