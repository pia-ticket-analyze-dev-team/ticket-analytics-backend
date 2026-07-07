package com.grup6.telco_ticket_analyzer.dto.analytics;

public record RegionDensityDto(
        String regionName,
        long ticketCount,
        double avgResolutionTimeHours,
        String densityLevel
) {
}
