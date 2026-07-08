package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.DailyTicketVolumeDto;
import com.grup6.telco_ticket_analyzer.dto.KpiSummaryDto;
import com.grup6.telco_ticket_analyzer.dto.NamedCountDto;
import com.grup6.telco_ticket_analyzer.dto.StatusCountDto;
import com.grup6.telco_ticket_analyzer.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/kpi-summary")
    public KpiSummaryDto getKpiSummary() {
        return dashboardService.getKpiSummary();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/charts/volume")
    public List<DailyTicketVolumeDto> getTicketVolume() {
        return dashboardService.getTicketVolumeLast30Days();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/charts/status")
    public List<StatusCountDto> getTicketsByStatus() {
        return dashboardService.getTicketsByStatus();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/charts/region")
    public List<NamedCountDto> getTicketsByRegion() {
        return dashboardService.getTicketsByRegion();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/charts/topics")
    public List<NamedCountDto> getTopIssueTopics() {
        return dashboardService.getTopIssueTopics();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/charts/department-workload")
    public List<NamedCountDto> getDepartmentWorkload() {
        return dashboardService.getDepartmentWorkload();
    }
}
