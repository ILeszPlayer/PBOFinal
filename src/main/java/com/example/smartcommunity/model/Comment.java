package com.example.smartcommunity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String isiKomentar;

    @Column(nullable = false)
    private LocalDateTime tanggal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Pengguna user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comment_likes", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "user_id")
    private Set<Long> likedUserIds = new HashSet<>();

    private int likeCount = 0;

    public Comment() {}

    public Comment(String isiKomentar, Pengguna user, Complaint complaint) {
        this.isiKomentar = isiKomentar;
        this.user = user;
        this.complaint = complaint;
        this.tanggal = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (tanggal == null) tanggal = LocalDateTime.now();
    }

    @Override
    public String getSummary() {
        String preview = isiKomentar.length() > 50 ? isiKomentar.substring(0, 50) + "..." : isiKomentar;
        return "Komentar: \"" + preview + "\" - " + (user != null ? user.getNama() : "Unknown");
    }

    public String getIsiKomentar() { return isiKomentar; }
    public void setIsiKomentar(String isiKomentar) { this.isiKomentar = isiKomentar; }
    public LocalDateTime getTanggal() { return tanggal; }
    public void setTanggal(LocalDateTime tanggal) { this.tanggal = tanggal; }
    public Pengguna getUser() { return user; }
    public void setUser(Pengguna user) { this.user = user; }
    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) { this.complaint = complaint; }
    public Set<Long> getLikedUserIds() { return likedUserIds; }
    public void setLikedUserIds(Set<Long> likedUserIds) { this.likedUserIds = likedUserIds; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public void toggleLike(Long userId) {
        if (userId == null) throw new IllegalArgumentException("User ID tidak boleh null");
        if (!likedUserIds.remove(userId)) {
            likedUserIds.add(userId);
        }
        this.likeCount = likedUserIds.size();
    }
}
