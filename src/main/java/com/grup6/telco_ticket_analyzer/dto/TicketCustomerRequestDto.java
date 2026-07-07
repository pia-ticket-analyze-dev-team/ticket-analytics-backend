package com.grup6.telco_ticket_analyzer.dto;

public record TicketCustomerRequestDto(
        String firstName,
        String lastName,
        String email,
        String phone
) {
}