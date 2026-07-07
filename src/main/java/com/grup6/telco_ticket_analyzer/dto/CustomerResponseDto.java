package com.grup6.telco_ticket_analyzer.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CustomerResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String address,
        LocalDate birthdate,
        String phone,
        LocalDate createdAt,
        String segment
) {
}
