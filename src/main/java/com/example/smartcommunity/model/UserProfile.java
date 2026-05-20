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

    @Column(name = "no_telp")
    private String noTelp;

    @Column(name = "tanggal_lahir")
    private LocalDate tanggalLahir;

    public UserProfile() {}

    public UserProfile(User user, String nik, String alamat, String noTelp, LocalDate tanggalLahir) {
        this.user = user;
        this.nik = nik;
        this.alamat = alamat;
        this.noTelp = noTelp;
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
    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }
    public LocalDate getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(LocalDate tanggalLahir) { this.tanggalLahir = tanggalLahir; }
}
