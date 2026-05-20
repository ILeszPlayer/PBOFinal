package com.example.smartcommunity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateCommentRequest {

    @NotNull(message = "User ID wajib diisi")
    private Long userId;

    @NotNull(message = "Complaint ID wajib diisi")
    private Long complaintId;

    @NotBlank(message = "Isi komentar wajib diisi")
    private String isiKomentar;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }
    public String getIsiKomentar() { return isiKomentar; }
    public void setIsiKomentar(String isiKomentar) { this.isiKomentar = isiKomentar; }
}
