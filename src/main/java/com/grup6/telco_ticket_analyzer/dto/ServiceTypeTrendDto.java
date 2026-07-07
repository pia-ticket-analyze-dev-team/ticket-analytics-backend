package com.grup6.telco_ticket_analyzer.dto.analytics;

import java.time.YearMonth;
import java.util.UUID;

public record ServiceTypeTrendDto(
        UUID serviceTypeId,
        String serviceTypeName,
        YearMonth month,
        long ticketCount
) {
}