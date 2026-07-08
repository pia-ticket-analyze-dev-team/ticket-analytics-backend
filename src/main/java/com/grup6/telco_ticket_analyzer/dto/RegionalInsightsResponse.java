package com.grup6.telco_ticket_analyzer.dto.analytics;

import java.util.List;

public record RegionalInsightsResponse(
        RegionalKpiSummaryDto kpis,
        List<RegionDensityDto> regionDensity,
        List<CityDensityDto> cityDensity
) {
}
