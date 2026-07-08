package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.DailyTicketVolumeDto;
import com.grup6.telco_ticket_analyzer.dto.KpiSummaryDto;
import com.grup6.telco_ticket_analyzer.dto.NamedCountDto;
import com.grup6.telco_ticket_analyzer.dto.StatusCountDto;
import com.grup6.telco_ticket_analyzer.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Ana panelde gösterilen özet metrikleri ve grafik verilerini sağlayan uçlar")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "KPI özetini getir", description = "Dashboard'un üst kısmında gösterilen genel performans göstergelerinin (KPI) özetini döner.")
    @GetMapping("/kpi-summary")
    public KpiSummaryDto getKpiSummary() {
        return dashboardService.getKpiSummary();
    }

    @Operation(summary = "Ticket hacmi grafiği", description = "Son 30 güne ait günlük ticket hacmini döner.")
    @GetMapping("/charts/volume")
    public List<DailyTicketVolumeDto> getTicketVolume() {
        return dashboardService.getTicketVolumeLast30Days();
    }

    @Operation(summary = "Duruma göre ticket dağılımı", description = "Ticket'ların durumlarına (örn. açık, kapalı) göre sayısal dağılımını döner.")
    @GetMapping("/charts/status")
    public List<StatusCountDto> getTicketsByStatus() {
        return dashboardService.getTicketsByStatus();
    }

    @Operation(summary = "Bölgeye göre ticket dağılımı", description = "Ticket'ların bölgelere göre sayısal dağılımını döner.")
    @GetMapping("/charts/region")
    public List<NamedCountDto> getTicketsByRegion() {
        return dashboardService.getTicketsByRegion();
    }

    @Operation(summary = "En çok karşılaşılan konular", description = "En sık açılan ticket konularının (issue topic) sayısına göre sıralanmış listesini döner.")
    @GetMapping("/charts/topics")
    public List<NamedCountDto> getTopIssueTopics() {
        return dashboardService.getTopIssueTopics();
    }

    @Operation(summary = "Departman iş yükü", description = "Her departmana düşen açık ticket sayısını gösteren iş yükü dağılımını döner.")
    @GetMapping("/charts/department-workload")
    public List<NamedCountDto> getDepartmentWorkload() {
        return dashboardService.getDepartmentWorkload();
    }
}
