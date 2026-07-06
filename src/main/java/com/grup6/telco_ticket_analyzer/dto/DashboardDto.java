package com.grup6.telco_ticket_analyzer.dto;

import java.util.List;

public record DashboardDto(
        long totalCustomers,
        long totalTickets,
        long openTickets,
        double slaBreachRatePct,
        double avgResolutionTimeHours,
        double avgCustomerSatisfaction,
        List<DailyTicketVolumeDto> ticketVolumeLast30Days,
        List<StatusCountDto> ticketsByStatus,
        List<NamedCountDto> ticketsByRegion,
        List<NamedCountDto> topIssueTopics,
        List<NamedCountDto> departmentWorkload
) {
}
