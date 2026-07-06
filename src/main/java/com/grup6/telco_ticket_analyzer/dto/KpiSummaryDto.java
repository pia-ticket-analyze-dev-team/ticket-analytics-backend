package com.grup6.telco_ticket_analyzer.dto;

public record KpiSummaryDto(
        KpiMetricDto totalCustomers,
        KpiMetricDto totalTickets,
        KpiMetricDto openTickets,
        KpiMetricDto avgResolutionTimeHours,
        KpiMetricDto avgCustomerSatisfaction
) {
}
