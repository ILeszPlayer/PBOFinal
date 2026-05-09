package com.example.smartcommunity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateComplaintRequest {
    @NotNull public Long userId;
    @NotBlank public String judul;
    @NotBlank public String isiPengaduan;
}
