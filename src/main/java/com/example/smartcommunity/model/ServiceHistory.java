package com.example.smartcommunity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "service_history")
public class ServiceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistory;
    private LocalDate tanggalUpdate = LocalDate.now();
    @Column(columnDefinition = "TEXT")
    private String keterangan;

    @ManyToOne
    @JoinColumn(name = "id_request")
    @JsonIgnore
    private ServiceRequest serviceRequest;

    public Long getIdHistory() { return idHistory; }
    public void setIdHistory(Long idHistory) { this.idHistory = idHistory; }
    public LocalDate getTanggalUpdate() { return tanggalUpdate; }
    public void setTanggalUpdate(LocalDate tanggalUpdate) { this.tanggalUpdate = tanggalUpdate; }
    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
    public ServiceRequest getServiceRequest() { return serviceRequest; }
    public void setServiceRequest(ServiceRequest serviceRequest) { this.serviceRequest = serviceRequest; }
}
