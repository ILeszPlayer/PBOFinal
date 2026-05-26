package com.example.smartcommunity.dto;

import jakarta.validation.constraints.NotNull;

public class PollVoteRequest {

    @NotNull
    private Long pollId;

    @NotNull
    private String vote;

    private Long userId;

    public Long getPollId() { return pollId; }
    public void setPollId(Long pollId) { this.pollId = pollId; }
    public String getVote() { return vote; }
    public void setVote(String vote) { this.vote = vote; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
