package com.example.smartcommunity.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateDocumentRequest {
    @NotBlank public String namaFile;
    @NotBlank public String fileUrl;
    @NotBlank public String jenisDokumen;
}
