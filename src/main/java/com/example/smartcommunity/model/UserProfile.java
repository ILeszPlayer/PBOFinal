package com.example.smartcommunity.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true, length = 16)
    private String nik;

    private String alamat;

    @Column(name = "nomor_telepon")
    private String nomorTelepon;

    @Column(name = "tanggal_lahir")
    private LocalDate tanggalLahir;

    public UserProfile() {}

    public UserProfile(User user, String nik, String alamat, String nomorTelepon, LocalDate tanggalLahir) {
        this.user = user;
        this.nik = nik;
        this.alamat = alamat;
        this.nomorTelepon = nomorTelepon;
        this.tanggalLahir = tanggalLahir;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String nomorTelepon) { this.nomorTelepon = nomorTelepon; }
    public LocalDate getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(LocalDate tanggalLahir) { this.tanggalLahir = tanggalLahir; }
}
