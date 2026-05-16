package com.example.smartcommunity.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_requests")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "request_type")

public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_request")
    private Long id;

    private String nomorRequest;

    private String status;

    private LocalDate tanggalPengajuan;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_service")
    private AdministrativeService administrativeService;

    public String processRequest() {
        return "Request diproses";
    }

    // Getter Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomorRequest() {
        return nomorRequest;
    }

    public void setNomorRequest(String nomorRequest) {
        this.nomorRequest = nomorRequest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getTanggalPengajuan() {
        return tanggalPengajuan;
    }

    public void setTanggalPengajuan(LocalDate tanggalPengajuan) {
        this.tanggalPengajuan = tanggalPengajuan;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AdministrativeService getAdministrativeService() {
        return administrativeService;
    }

    public void setAdministrativeService(
            AdministrativeService administrativeService) {

        this.administrativeService = administrativeService;
    }
}