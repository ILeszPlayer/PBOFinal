package com.example.smartcommunity.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
public class Complaint {

    public enum Kategori {
        INFRASTRUKTUR, KEBERSIHAN, KEAMANAN, KESEHATAN, LAIN_LAIN
    }

    public enum Status {
        MENUNGGU, DIPROSES, SELESAI
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String judul;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String isiPengaduan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Kategori kategori;

    @Column(nullable = false)
    private LocalDateTime tanggal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "bukti_foto")
    private String buktiFoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("tanggal ASC")
    private List<Comment> comments = new ArrayList<>();

    public Complaint() {}

    public Complaint(String judul, String isiPengaduan, Kategori kategori, User user) {
        this.judul = judul;
        this.isiPengaduan = isiPengaduan;
        this.kategori = kategori;
        this.user = user;
        this.tanggal = LocalDateTime.now();
        this.status = Status.MENUNGGU;
    }

    @PrePersist
    protected void onCreate() {
        if (tanggal == null) tanggal = LocalDateTime.now();
        if (status == null) status = Status.MENUNGGU;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getIsiPengaduan() { return isiPengaduan; }
    public void setIsiPengaduan(String isiPengaduan) { this.isiPengaduan = isiPengaduan; }
    public Kategori getKategori() { return kategori; }
    public void setKategori(Kategori kategori) { this.kategori = kategori; }
    public LocalDateTime getTanggal() { return tanggal; }
    public void setTanggal(LocalDateTime tanggal) { this.tanggal = tanggal; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getBuktiFoto() { return buktiFoto; }
    public void setBuktiFoto(String buktiFoto) { this.buktiFoto = buktiFoto; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}
