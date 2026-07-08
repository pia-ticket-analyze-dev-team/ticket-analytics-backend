package com.grup6.telco_ticket_analyzer.dto.auth;

public record LoginRequestDto(
        String email,
        String password
) {
}
