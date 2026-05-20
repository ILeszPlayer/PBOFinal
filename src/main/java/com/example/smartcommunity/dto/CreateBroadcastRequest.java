package com.example.smartcommunity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateBroadcastRequest {

    @NotNull(message = "Admin ID wajib diisi")
    private Long adminId;

    @NotBlank(message = "Judul wajib diisi")
    private String judul;

    @NotBlank(message = "Isi broadcast wajib diisi")
    private String isiBroadcast;

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getIsiBroadcast() { return isiBroadcast; }
    public void setIsiBroadcast(String isiBroadcast) { this.isiBroadcast = isiBroadcast; }
}
