package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.analytics.RegionalInsightsResponse;
import com.grup6.telco_ticket_analyzer.service.analytics.RegionalInsightsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/regional-insights")
@RequiredArgsConstructor
@Tag(name = "Bölgesel İçgörüler", description = "Bölgelere göre ticket ve performans verilerinin özetlendiği içgörü uçları")
public class RegionalInsightsController {

    private final RegionalInsightsService regionalInsightsService;

    @Operation(summary = "Bölgesel içgörüleri getir", description = "Tüm bölgeler için ticket hacmi, SLA performansı gibi metrikleri içeren özet içgörü verisini döner.")
    @GetMapping
    public RegionalInsightsResponse getRegionalInsights() {
        return regionalInsightsService.getRegionalInsights();
    }
}
