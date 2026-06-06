package com.example.smartcommunity.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Pengguna user;

    @Column(unique = true, length = 16)
    private String nik;

    private String alamat;

    @Column(name = "nomor_telepon")
    private String nomorTelepon;

    @Column(name = "tanggal_lahir")
    private LocalDate tanggalLahir;

    public UserProfile() {}

    public UserProfile(Pengguna user, String nik, String alamat, String nomorTelepon, LocalDate tanggalLahir) {
        this.user = user;
        this.nik = nik;
        this.alamat = alamat;
        this.nomorTelepon = nomorTelepon;
        this.tanggalLahir = tanggalLahir;
    }

    @Override
    public String getSummary() {
        return "Profil: " + (user != null ? user.getNama() : "?") + " - NIK: " + (nik != null ? nik : "-");
    }

    public Pengguna getUser() { return user; }
    public void setUser(Pengguna user) { this.user = user; }
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String nomorTelepon) { this.nomorTelepon = nomorTelepon; }
    public LocalDate getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(LocalDate tanggalLahir) { this.tanggalLahir = tanggalLahir; }
}
