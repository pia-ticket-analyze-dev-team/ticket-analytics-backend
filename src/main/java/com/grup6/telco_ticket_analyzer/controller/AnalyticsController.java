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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/customer-churn-risk")
    public List<CustomerChurnRiskDto> getCustomerChurnRisk() {
        return customerChurnRiskService.getCustomerChurnRisk();
    }

    @GetMapping("/agent-performance")
    public List<AgentPerformanceDto> getAgentPerformance() {
        return agentPerformanceService.getAgentPerformance();
    }

    @GetMapping("/sla-target-rate")
    public SlaTargetRateDto getSlaTargetRate() {
        return slaTargetRateService.getSlaTargetRate();
    }

    @GetMapping("/service-type-trend")
    public List<ServiceTypeTrendDto> getServiceTypeTrend() {
        return serviceTypeTrendService.getServiceTypeTrend();
    }
}