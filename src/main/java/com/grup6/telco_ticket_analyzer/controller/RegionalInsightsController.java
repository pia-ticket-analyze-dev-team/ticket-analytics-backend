package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.analytics.RegionalInsightsResponse;
import com.grup6.telco_ticket_analyzer.service.analytics.RegionalInsightsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/regional-insights")
@RequiredArgsConstructor
public class RegionalInsightsController {

    private final RegionalInsightsService regionalInsightsService;

    @GetMapping
    public RegionalInsightsResponse getRegionalInsights() {
        return regionalInsightsService.getRegionalInsights();
    }
}
