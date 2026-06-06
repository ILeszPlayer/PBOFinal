package com.example.smartcommunity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    public enum Type {
        STATUS_CHANGE,
        NEW_COMMENT,
        NEW_COMPLAINT,
        SYSTEM
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"complaints", "comments", "notifications", "profile", "password", "hibernateLazyInitializer", "handler"})
    private Pengguna user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id")
    @JsonIgnoreProperties({"comments", "user", "hibernateLazyInitializer", "handler"})
    private Complaint complaint;

    public Notification() {}

    public Notification(Type type, String message, Pengguna user, Complaint complaint) {
        this.type = type;
        this.message = message;
        this.user = user;
        this.complaint = complaint;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @Override
    public String getSummary() {
        String preview = message.length() > 50 ? message.substring(0, 50) + "..." : message;
        return "[" + (getType() != null ? getType().name() : "?") + "] " + preview;
    }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isIsRead() { return isRead; }
    public void setIsRead(boolean isRead) { this.isRead = isRead; }

    public void markAsRead() {
        this.isRead = true;
    }

    public Pengguna getUser() { return user; }
    public void setUser(Pengguna user) { this.user = user; }
    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) { this.complaint = complaint; }
}
