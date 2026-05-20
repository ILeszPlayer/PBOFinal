package com.example.smartcommunity.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateBroadcastRequest {

    private Long adminId;

    @NotBlank
    private String judul;

    @NotBlank
    private String isiBroadcast;

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getIsiBroadcast() { return isiBroadcast; }
    public void setIsiBroadcast(String isiBroadcast) { this.isiBroadcast = isiBroadcast; }
}
