package com.grup6.telco_ticket_analyzer.dto.analytics;

public record RegionalKpiSummaryDto(
        long totalTickets,
        long activeCities,
        double successRate,
        double avgResolutionTimeHours
) {
}
