package com.example.smartcommunity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateUserRequest {
    @NotBlank public String nama;
    @Email public String email;
    @NotBlank public String password;
    @NotBlank public String role;
}
