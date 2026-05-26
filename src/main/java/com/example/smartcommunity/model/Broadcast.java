package com.example.smartcommunity.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "broadcasts")
public class Broadcast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String judul;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String isiBroadcast;

    @Column(nullable = false)
    private LocalDateTime tanggal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Pengguna admin;

    public Broadcast() {}

    public Broadcast(String judul, String isiBroadcast, Pengguna admin) {
        this.judul = judul;
        this.isiBroadcast = isiBroadcast;
        this.admin = admin;
        this.tanggal = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (tanggal == null) tanggal = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getIsiBroadcast() { return isiBroadcast; }
    public void setIsiBroadcast(String isiBroadcast) { this.isiBroadcast = isiBroadcast; }
    public LocalDateTime getTanggal() { return tanggal; }
    public void setTanggal(LocalDateTime tanggal) { this.tanggal = tanggal; }
    public Pengguna getAdmin() { return admin; }
    public void setAdmin(Pengguna admin) { this.admin = admin; }
}
