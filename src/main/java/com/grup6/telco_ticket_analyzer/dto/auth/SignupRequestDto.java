package com.grup6.telco_ticket_analyzer.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SignupRequestDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String roleCode,
        UUID agentId
) {
}
