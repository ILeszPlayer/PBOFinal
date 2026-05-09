package com.example.smartcommunity.dto;

import jakarta.validation.constraints.NotNull;

public class CreateServiceRequestDto {
    @NotNull public Long userId;
    @NotNull public Long serviceId;
    public String catatanAdmin;
}
