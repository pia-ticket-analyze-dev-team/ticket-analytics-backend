package com.grup6.telco_ticket_analyzer.dto;

public record StatusCountDto(
        String status,
        long ticketCount,
        double pctOfTotal
) {
}
