package com.example.smartcommunity.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "poll_votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"poll_id", "user_id"})
})
public class PollVote extends BaseEntity {

    public enum Vote {
        YES, NO
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Vote vote;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Pengguna user;

    public PollVote() {}

    public PollVote(Vote vote, Poll poll, Pengguna user) {
        this.vote = vote;
        this.poll = poll;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @Override
    public String getSummary() {
        return "Vote " + getVote() + " oleh " + (user != null ? user.getNama() : "?");
    }

    public Vote getVote() { return vote; }
    public void setVote(Vote vote) { this.vote = vote; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Poll getPoll() { return poll; }
    public void setPoll(Poll poll) { this.poll = poll; }
    public Pengguna getUser() { return user; }
    public void setUser(Pengguna user) { this.user = user; }
}
