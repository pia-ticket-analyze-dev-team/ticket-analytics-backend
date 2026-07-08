package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.analytics.AgentPerformanceDto;
import com.grup6.telco_ticket_analyzer.dto.analytics.CustomerChurnRiskDto;
import com.grup6.telco_ticket_analyzer.dto.analytics.ServiceTypeTrendDto;
import com.grup6.telco_ticket_analyzer.dto.analytics.SlaTargetRateDto;
import com.grup6.telco_ticket_analyzer.service.analytics.AgentPerformanceService;
import com.grup6.telco_ticket_analyzer.service.analytics.CustomerChurnRiskService;
import com.grup6.telco_ticket_analyzer.service.analytics.ServiceTypeTrendService;
import com.grup6.telco_ticket_analyzer.service.analytics.SlaTargetRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analitik", description = "Müşteri kayıp riski, temsilci performansı, SLA hedef oranı ve servis tipi trendleri gibi analiz uçları")
public class AnalyticsController {

    private final CustomerChurnRiskService customerChurnRiskService;
    private final AgentPerformanceService agentPerformanceService;
    private final SlaTargetRateService slaTargetRateService;
    private final ServiceTypeTrendService serviceTypeTrendService;

    @Operation(summary = "Müşteri kayıp riski listesi", description = "Segmente ve risk seviyesine göre filtrelenebilen, müşterilerin kayıp (churn) riskini gösteren sayfalanmış listeyi döner.")
    @GetMapping("/customer-churn-risk")
    public Page<CustomerChurnRiskDto> getCustomerChurnRisk(
            @Parameter(description = "Sayfa numarası (0'dan başlar)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa başına kayıt sayısı") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Müşteri segmentine göre filtre") @RequestParam(required = false) String segment,
            @Parameter(description = "Risk seviyesine göre filtre (örn. LOW, MEDIUM, HIGH)") @RequestParam(required = false) String riskLevel
    ) {
        return customerChurnRiskService.getCustomerChurnRisk(page, size, segment, riskLevel);
    }

    @Operation(summary = "Temsilci performans listesi", description = "Temsilcilerin (agent) çözdükleri ticket sayısı, ortalama çözüm süresi gibi performans metriklerini sayfalanmış olarak döner.")
    @GetMapping("/agent-performance")
    public Page<AgentPerformanceDto> getAgentPerformance(
            @Parameter(description = "Sayfa numarası (0'dan başlar)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa başına kayıt sayısı") @RequestParam(defaultValue = "10") int size
    ) {
        return agentPerformanceService.getAgentPerformance(page, size);
    }

    @Operation(summary = "SLA hedef oranı", description = "Tüm ticket'lar genelinde SLA (hizmet seviyesi anlaşması) hedefinin ne oranda tutturulduğunu döner.")
    @GetMapping("/sla-target-rate")
    public SlaTargetRateDto getSlaTargetRate() {
        return slaTargetRateService.getSlaTargetRate();
    }

    @Operation(summary = "Servis tipi trend listesi", description = "Servis tiplerine göre ticket hacminin zaman içindeki değişim trendini döner.")
    @GetMapping("/service-type-trend")
    public List<ServiceTypeTrendDto> getServiceTypeTrend() {
        return serviceTypeTrendService.getServiceTypeTrend();
    }
}