package com.example.smartcommunity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class Pengguna extends BaseEntity {

    @Column(nullable = false)
    private String nama;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "reputation_points", nullable = false)
    private int reputationPoints = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Complaint> complaints = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"user", "complaint"})
    private List<Notification> notifications = new ArrayList<>();

    @JsonIgnore
    public abstract String getDashboardRoute();

    @JsonIgnore
    public abstract String getRoleDisplayName();

    @Override
    public String getSummary() {
        return getNama() + " [" + getRole() + "] - " + getReputationTier();
    }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getEmail() { return email; }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Format email tidak valid: " + email);
        }
        this.email = email;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getReputationPoints() { return reputationPoints; }
    public void setReputationPoints(int reputationPoints) { this.reputationPoints = reputationPoints; }

    public void addReputationPoints(int points) {
        if (points < 0) throw new IllegalArgumentException("Points tidak boleh negatif");
        this.reputationPoints += points;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public UserProfile getProfile() { return profile; }
    public void setProfile(UserProfile profile) { this.profile = profile; }
    public List<Complaint> getComplaints() { return complaints; }
    public void setComplaints(List<Complaint> complaints) { this.complaints = complaints; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }

    public String getReputationTier() {
        if (reputationPoints >= 200) return "Pahlawan Komunitas";
        if (reputationPoints >= 100) return "Warga Teladan";
        if (reputationPoints >= 50)  return "Warga Peduli";
        if (reputationPoints >= 20)  return "Warga Aktif";
        return "Warga Baru";
    }
}
