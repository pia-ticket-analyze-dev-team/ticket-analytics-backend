package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.analytics.AgentPerformanceDto;
import com.grup6.telco_ticket_analyzer.dto.analytics.CustomerChurnRiskDto;
import com.grup6.telco_ticket_analyzer.dto.analytics.ServiceTypeTrendDto;
import com.grup6.telco_ticket_analyzer.dto.analytics.SlaTargetRateDto;
import com.grup6.telco_ticket_analyzer.service.analytics.AgentPerformanceService;
import com.grup6.telco_ticket_analyzer.service.analytics.CustomerChurnRiskService;
import com.grup6.telco_ticket_analyzer.service.analytics.ServiceTypeTrendService;
import com.grup6.telco_ticket_analyzer.service.analytics.SlaTargetRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final CustomerChurnRiskService customerChurnRiskService;
    private final AgentPerformanceService agentPerformanceService;
    private final SlaTargetRateService slaTargetRateService;
    private final ServiceTypeTrendService serviceTypeTrendService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customer-churn-risk")
    public Page<CustomerChurnRiskDto> getCustomerChurnRisk(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return customerChurnRiskService.getCustomerChurnRisk(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/agent-performance")
    public Page<AgentPerformanceDto> getAgentPerformance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return agentPerformanceService.getAgentPerformance(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sla-target-rate")
    public SlaTargetRateDto getSlaTargetRate() {
        return slaTargetRateService.getSlaTargetRate();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/service-type-trend")
    public List<ServiceTypeTrendDto> getServiceTypeTrend() {
        return serviceTypeTrendService.getServiceTypeTrend();
    }
}
