package com.example.smartcommunity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfile;

    private String nik;
    private String alamat;
    private String nomorTelepon;
    private LocalDate tanggalLahir;

    @OneToOne
    @JoinColumn(name = "id_user")
    @JsonIgnore
    private User user;

    public Long getIdProfile() { return idProfile; }
    public void setIdProfile(Long idProfile) { this.idProfile = idProfile; }
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String nomorTelepon) { this.nomorTelepon = nomorTelepon; }
    public LocalDate getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(LocalDate tanggalLahir) { this.tanggalLahir = tanggalLahir; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
