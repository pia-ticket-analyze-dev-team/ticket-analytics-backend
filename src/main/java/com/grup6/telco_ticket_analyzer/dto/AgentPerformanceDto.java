package com.grup6.telco_ticket_analyzer.dto.analytics;

import java.util.UUID;

public record AgentPerformanceDto(
        UUID agentId,
        String agentName,
        long resolvedTicketCount,
        double averageSatisfactionScore,
        double slaSuccessRate,
        double averageResolutionHours,
        double performanceScore
) {
}