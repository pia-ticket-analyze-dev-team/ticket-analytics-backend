package com.grup6.telco_ticket_analyzer.dto.auth;

import java.util.Set;
import java.util.UUID;

public record LoginResponseDto(
        String tokenType,
        String accessToken,
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String roleCode,
        Set<String> authorities
) {
}
