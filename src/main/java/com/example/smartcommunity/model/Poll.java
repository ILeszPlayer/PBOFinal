package com.example.smartcommunity.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polls")
public class Poll extends BaseEntity {

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Pengguna admin;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PollVote> votes = new ArrayList<>();

    public Poll() {}

    public Poll(String question, Pengguna admin) {
        this.question = question;
        this.admin = admin;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public long getYesCount() {
        return votes.stream().filter(v -> v.getVote() == PollVote.Vote.YES).count();
    }

    public long getNoCount() {
        return votes.stream().filter(v -> v.getVote() == PollVote.Vote.NO).count();
    }

    public long getTotalVotes() {
        return votes.size();
    }

    public double getYesPercentage() {
        long total = getTotalVotes();
        return total == 0 ? 0 : (getYesCount() * 100.0) / total;
    }

    public double getNoPercentage() {
        long total = getTotalVotes();
        return total == 0 ? 0 : (getNoCount() * 100.0) / total;
    }

    public boolean hasUserVoted(Long userId) {
        return votes.stream().anyMatch(v -> v.getUser().getId().equals(userId));
    }

    @Override
    public String getSummary() {
        String status = isActive ? "Aktif" : "Ditutup";
        return "Polling: \"" + getQuestion() + "\" (" + status + ") - " + getTotalVotes() + " suara";
    }

    public void close() {
        if (!this.isActive) {
            throw new IllegalStateException("Polling sudah ditutup sebelumnya");
        }
        this.isActive = false;
        this.closedAt = LocalDateTime.now();
    }

    public void toggleActive() {
        this.isActive = !this.isActive;
        if (!this.isActive) {
            this.closedAt = LocalDateTime.now();
        } else {
            this.closedAt = null;
        }
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
    public Pengguna getAdmin() { return admin; }
    public void setAdmin(Pengguna admin) { this.admin = admin; }
    public List<PollVote> getVotes() { return votes; }
    public void setVotes(List<PollVote> votes) { this.votes = votes; }
}
