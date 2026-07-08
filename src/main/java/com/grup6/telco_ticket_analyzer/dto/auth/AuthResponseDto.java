package com.grup6.telco_ticket_analyzer.dto.auth;

import java.util.UUID;

public record AuthResponseDto(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String roleCode,
        String roleName,
        UUID agentId,
        boolean active
) {
}
