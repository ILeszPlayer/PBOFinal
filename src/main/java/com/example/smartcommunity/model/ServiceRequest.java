package com.example.smartcommunity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_requests")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "request_type")
public abstract class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRequest;

    private String nomorRequest;
    private LocalDate tanggalPengajuan = LocalDate.now();
    private String status = "DIAJUKAN";

    @Column(columnDefinition = "TEXT")
    private String catatanAdmin;

    @ManyToOne
    @JoinColumn(name = "id_user")
    @JsonIgnoreProperties({"serviceRequests", "complaints", "profile", "password"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_service")
    @JsonIgnoreProperties("serviceRequests")
    private AdministrativeService administrativeService;

    @OneToMany(mappedBy = "serviceRequest", cascade = CascadeType.ALL)
    private List<ServiceHistory> histories = new ArrayList<>();

    @OneToMany(mappedBy = "serviceRequest", cascade = CascadeType.ALL)
    private List<ServiceDocument> documents = new ArrayList<>();

    public abstract String processRequest();

    public String submit() { return "Pengajuan layanan berhasil dikirim"; }
    public String cancel() { this.status = "DIBATALKAN"; return "Pengajuan layanan dibatalkan"; }

    public Long getIdRequest() { return idRequest; }
    public void setIdRequest(Long idRequest) { this.idRequest = idRequest; }
    public String getNomorRequest() { return nomorRequest; }
    public void setNomorRequest(String nomorRequest) { this.nomorRequest = nomorRequest; }
    public LocalDate getTanggalPengajuan() { return tanggalPengajuan; }
    public void setTanggalPengajuan(LocalDate tanggalPengajuan) { this.tanggalPengajuan = tanggalPengajuan; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCatatanAdmin() { return catatanAdmin; }
    public void setCatatanAdmin(String catatanAdmin) { this.catatanAdmin = catatanAdmin; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public AdministrativeService getAdministrativeService() { return administrativeService; }
    public void setAdministrativeService(AdministrativeService administrativeService) { this.administrativeService = administrativeService; }
    public List<ServiceHistory> getHistories() { return histories; }
    public void setHistories(List<ServiceHistory> histories) { this.histories = histories; }
    public List<ServiceDocument> getDocuments() { return documents; }
    public void setDocuments(List<ServiceDocument> documents) { this.documents = documents; }
}
