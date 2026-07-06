package com.grup6.telco_ticket_analyzer.dto.analytics;

public record SlaTargetRateDto(
        long totalTicketCount,
        long breachedTicketCount,
        long withinSlaTicketCount,
        double slaTargetRate
) {
}