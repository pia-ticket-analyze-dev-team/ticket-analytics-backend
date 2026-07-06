package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.DailyTicketVolumeDto;
import com.grup6.telco_ticket_analyzer.dto.KpiSummaryDto;
import com.grup6.telco_ticket_analyzer.dto.NamedCountDto;
import com.grup6.telco_ticket_analyzer.dto.StatusCountDto;
import com.grup6.telco_ticket_analyzer.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/kpi-summary")
    public KpiSummaryDto getKpiSummary() {
        return dashboardService.getKpiSummary();
    }

    @GetMapping("/charts/volume")
    public List<DailyTicketVolumeDto> getTicketVolume() {
        return dashboardService.getTicketVolumeLast30Days();
    }

    @GetMapping("/charts/status")
    public List<StatusCountDto> getTicketsByStatus() {
        return dashboardService.getTicketsByStatus();
    }

    @GetMapping("/charts/region")
    public List<NamedCountDto> getTicketsByRegion() {
        return dashboardService.getTicketsByRegion();
    }

    @GetMapping("/charts/topics")
    public List<NamedCountDto> getTopIssueTopics() {
        return dashboardService.getTopIssueTopics();
    }

    @GetMapping("/charts/department-workload")
    public List<NamedCountDto> getDepartmentWorkload() {
        return dashboardService.getDepartmentWorkload();
    }
}
