package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.DailyTicketVolumeDto;
import com.grup6.telco_ticket_analyzer.dto.DashboardDto;
import com.grup6.telco_ticket_analyzer.dto.NamedCountDto;
import com.grup6.telco_ticket_analyzer.dto.StatusCountDto;
import com.grup6.telco_ticket_analyzer.repository.DashboardRepository;
import com.grup6.telco_ticket_analyzer.repository.projection.TicketStatusCountProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardDto getDashboard() {
        long totalTickets = orZero(dashboardRepository.getTotalTicketCount());

        List<StatusCountDto> ticketsByStatus = dashboardRepository.getTicketStatusDistribution().stream()
                .map(p -> toStatusCountDto(p, totalTickets))
                .toList();

        List<DailyTicketVolumeDto> ticketVolume = dashboardRepository.getTicketVolumeLast30Days().stream()
                .map(p -> new DailyTicketVolumeDto(p.getDay(), orZero(p.getTicketCount())))
                .toList();

        List<NamedCountDto> ticketsByRegion = dashboardRepository.getTopRegionsByTicketCount().stream()
                .map(p -> new NamedCountDto(p.getName(), orZero(p.getTicketCount())))
                .toList();

        List<NamedCountDto> topIssueTopics = dashboardRepository.getTopIssueTopics().stream()
                .map(p -> new NamedCountDto(p.getName(), orZero(p.getTicketCount())))
                .toList();

        List<NamedCountDto> departmentWorkload = dashboardRepository.getDepartmentWorkload().stream()
                .map(p -> new NamedCountDto(p.getName(), orZero(p.getTicketCount())))
                .toList();

        return new DashboardDto(
                orZero(dashboardRepository.getTotalCustomerCount()),
                totalTickets,
                orZero(dashboardRepository.getOpenTicketCount()),
                orZero(dashboardRepository.getSlaBreachRatePct()),
                orZero(dashboardRepository.getAverageResolutionTimeHours()),
                orZero(dashboardRepository.getAverageCustomerSatisfaction()),
                ticketVolume,
                ticketsByStatus,
                ticketsByRegion,
                topIssueTopics,
                departmentWorkload
        );
    }

    private static StatusCountDto toStatusCountDto(TicketStatusCountProjection projection, long totalTickets) {
        long ticketCount = orZero(projection.getTicketCount());
        double pctOfTotal = totalTickets == 0 ? 0.0 : Math.round(10000.0 * ticketCount / totalTickets) / 100.0;
        return new StatusCountDto(projection.getStatus(), ticketCount, pctOfTotal);
    }

    private static long orZero(Long value) {
        return value != null ? value : 0L;
    }

    private static double orZero(Double value) {
        return value != null ? value : 0.0;
    }
}
