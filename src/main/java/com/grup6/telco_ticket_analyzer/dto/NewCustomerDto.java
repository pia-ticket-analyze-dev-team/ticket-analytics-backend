package com.grup6.telco_ticket_analyzer.dto;

import java.time.LocalDate;

public record NewCustomerDto(
        String firstName,
        String lastName,
        String email,
        String address,
        LocalDate birthdate,
        String phone,
        String segment
) {
}
