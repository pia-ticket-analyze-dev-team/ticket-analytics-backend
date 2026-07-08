package com.grup6.telco_ticket_analyzer.dto.auth;

import java.util.UUID;

public record LoginResponseDto(
        UUID userId,
        String email,
        String fullName,
        String role,
        UUID agentId,
        String departmentCode
) {
}
