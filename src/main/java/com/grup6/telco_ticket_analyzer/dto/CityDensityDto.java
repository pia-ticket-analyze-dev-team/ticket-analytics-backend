package com.grup6.telco_ticket_analyzer.dto.analytics;

public record CityDensityDto(
        int rank,
        String cityName,
        long ticketCount,
        double avgResolutionTimeHours,
        String densityLevel
) {
}
