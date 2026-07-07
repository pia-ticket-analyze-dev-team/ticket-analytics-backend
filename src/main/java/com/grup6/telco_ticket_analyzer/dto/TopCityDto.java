package com.grup6.telco_ticket_analyzer.dto.analytics;

public record TopCityDto(
        int rank,
        String cityName,
        long ticketCount,
        double avgResolutionTimeHours
) {
}
