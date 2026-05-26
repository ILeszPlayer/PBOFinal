package com.example.smartcommunity.dto;

import jakarta.validation.constraints.NotBlank;

public class CreatePollRequest {

    private Long adminId;

    @NotBlank
    private String question;

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}
