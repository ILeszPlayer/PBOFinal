package com.example.smartcommunity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComplaint;

    @NotBlank
    private String judul;

    @Column(columnDefinition = "TEXT")
    private String isiPengaduan;

    private LocalDate tanggal = LocalDate.now();
    private String status = "MENUNGGU";

    @ManyToOne
    @JoinColumn(name = "id_user")
    @JsonIgnoreProperties({"complaints", "serviceRequests", "profile", "password"})
    private User user;

    public Long getIdComplaint() { return idComplaint; }
    public void setIdComplaint(Long idComplaint) { this.idComplaint = idComplaint; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getIsiPengaduan() { return isiPengaduan; }
    public void setIsiPengaduan(String isiPengaduan) { this.isiPengaduan = isiPengaduan; }
    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
