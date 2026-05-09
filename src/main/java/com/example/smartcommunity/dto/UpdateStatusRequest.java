package com.example.smartcommunity.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateStatusRequest {
    @NotBlank public String status;
    public String keterangan;
}
