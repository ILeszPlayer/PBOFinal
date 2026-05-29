package com.example.smartcommunity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "complaints")
public class Complaint {

    public enum Kategori {
        INFRASTRUKTUR("Infrastruktur"),
        LINGKUNGAN("Lingkungan"),
        SOSIAL("Sosial"),
        KEAMANAN("Keamanan"),
        KESEHATAN("Kesehatan"),
        PENDIDIKAN("Pendidikan"),
        UMUM("Umum");

        private final String displayName;
        Kategori(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum Status {
        PENDING("Pending"),
        PROSES("Diproses"),
        SELESAI("Selesai");

        private final String displayName;
        Status(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum Urgency {
        RENDAH("Rendah"),
        SEDANG("Sedang"),
        TINGGI("Tinggi");

        private final String displayName;
        Urgency(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "judul", nullable = false)
    private String judul;

    @Column(name = "isi_pengaduan", columnDefinition = "TEXT", nullable = false)
    private String isiPengaduan;

    @Enumerated(EnumType.STRING)
    @Column(name = "kategori", nullable = false)
    private Kategori kategori;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency", nullable = false)
    private Urgency urgency = Urgency.SEDANG;

    @Column(name = "tanggal", nullable = false)
    private LocalDateTime tanggal;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "bukti_foto")
    private String buktiFoto;

    @Column(name = "upvotes_count", nullable = false)
    private int upvotesCount = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "complaint_upvotes", joinColumns = @JoinColumn(name = "complaint_id"))
    @Column(name = "user_id")
    @JsonIgnore
    private Set<Long> upvotedUserIds = new HashSet<>();

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous = false;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "lokasi_nama")
    private String lokasiNama;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"complaints", "comments", "notifications", "profile", "password", "hibernateLazyInitializer", "handler"})
    private Pengguna user;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("tanggal ASC")
    @JsonIgnoreProperties({"complaint", "hibernateLazyInitializer", "handler"})
    private List<Comment> comments = new ArrayList<>();

    public Complaint() {}

    @PrePersist
    protected void onCreate() {
        if (tanggal == null) tanggal = LocalDateTime.now();
        if (status == null) status = Status.PENDING;
        if (urgency == null) urgency = Urgency.SEDANG;
    }

    public long getSlaHours() {
        if (resolvedAt == null || processedAt == null) return 0;
        return java.time.Duration.between(processedAt, resolvedAt).toHours();
    }

    public boolean isSlaCompliant() {
        if (status != Status.SELESAI) return false;
        return getSlaHours() <= 24;
    }

    public String getDisplayUser() {
        if (isAnonymous) return "Anonim";
        return user != null ? user.getNama() : "Unknown";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getIsiPengaduan() { return isiPengaduan; }
    public void setIsiPengaduan(String isiPengaduan) { this.isiPengaduan = isiPengaduan; }
    public Kategori getKategori() { return kategori; }
    public void setKategori(Kategori kategori) { this.kategori = kategori; }
    public Urgency getUrgency() { return urgency; }
    public void setUrgency(Urgency urgency) { this.urgency = urgency; }
    public LocalDateTime getTanggal() { return tanggal; }
    public void setTanggal(LocalDateTime tanggal) { this.tanggal = tanggal; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getBuktiFoto() { return buktiFoto; }
    public void setBuktiFoto(String buktiFoto) { this.buktiFoto = buktiFoto; }
    public int getUpvotesCount() { return upvotesCount; }
    public void setUpvotesCount(int upvotesCount) { this.upvotesCount = upvotesCount; }
    public Set<Long> getUpvotedUserIds() { return upvotedUserIds; }
    public void setUpvotedUserIds(Set<Long> upvotedUserIds) { this.upvotedUserIds = upvotedUserIds; }
    public boolean isIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getLokasiNama() { return lokasiNama; }
    public void setLokasiNama(String lokasiNama) { this.lokasiNama = lokasiNama; }
    public Pengguna getUser() { return user; }
    public void setUser(Pengguna user) { this.user = user; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}
