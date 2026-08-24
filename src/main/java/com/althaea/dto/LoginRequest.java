package com.althaea.dto;
import jakarta.validation.constraints.*;
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}
