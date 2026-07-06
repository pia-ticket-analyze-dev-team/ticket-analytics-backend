package com.grup6.telco_ticket_analyzer.dto;

public record KpiMetricDto(
        double currentValue,
        double previousValue,
        double changeAbsolute,
        double changePercentage
) {
}
