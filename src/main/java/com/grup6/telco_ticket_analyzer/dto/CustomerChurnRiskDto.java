package com.grup6.telco_ticket_analyzer.dto.analytics;

import java.util.UUID;

public record CustomerChurnRiskDto(
        UUID customerId,
        String customerName,
        String customerSegment,
        long ticketCount,
        double averageSatisfactionScore,
        double slaBreachRate,
        double averageResolutionHours,
        double churnRiskScore,
        String riskLevel
) {
}