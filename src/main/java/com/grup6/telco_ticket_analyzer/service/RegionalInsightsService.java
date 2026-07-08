package com.grup6.telco_ticket_analyzer.service.analytics;

import com.grup6.telco_ticket_analyzer.dto.analytics.CityDensityDto;
import com.grup6.telco_ticket_analyzer.dto.analytics.RegionDensityDto;
import com.grup6.telco_ticket_analyzer.dto.analytics.RegionalInsightsResponse;
import com.grup6.telco_ticket_analyzer.dto.analytics.RegionalKpiSummaryDto;
import com.grup6.telco_ticket_analyzer.model.Region;
import com.grup6.telco_ticket_analyzer.repository.RegionRepository;
import com.grup6.telco_ticket_analyzer.repository.TicketRepository;
import com.grup6.telco_ticket_analyzer.repository.projection.ResolutionTimeProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionalInsightsService {

    private static final double HIGH_DENSITY_RATIO = 0.66;
    private static final double MEDIUM_DENSITY_RATIO = 0.33;

    private final TicketRepository ticketRepository;
    private final RegionRepository regionRepository;

    public RegionalInsightsResponse getRegionalInsights() {
        List<Region> regions = regionRepository.findAll();

        List<NamedResolutionStat> cityStats = regions.stream()
                .map(region -> new NamedResolutionStat(
                        region.getCityName(),
                        orZero(ticketRepository.countByRegionId(region.getId())),
                        averageResolutionTimeHours(ticketRepository.findByRegionIdAndResolutionTimeHoursIsNotNull(region.getId()))
                ))
                .toList();

        List<NamedResolutionStat> geographicalRegionStats = regions.stream()
                .map(Region::getGeographicalRegion)
                .distinct()
                .map(geographicalRegion -> new NamedResolutionStat(
                        geographicalRegion,
                        ticketRepository.countByRegion_GeographicalRegion(geographicalRegion),
                        averageResolutionTimeHours(ticketRepository.findByRegion_GeographicalRegionAndResolutionTimeHoursIsNotNull(geographicalRegion))
                ))
                .toList();

        return new RegionalInsightsResponse(
                buildKpiSummary(cityStats),
                buildRegionDensity(geographicalRegionStats),
                buildCityDensity(cityStats)
        );
    }

    private RegionalKpiSummaryDto buildKpiSummary(List<NamedResolutionStat> cityStats) {
        long totalTickets = ticketRepository.count();
        long activeCities = cityStats.stream().filter(stat -> stat.ticketCount() > 0).count();
        long breachedCount = ticketRepository.countBySlaBreached(true);
        double successRate = totalTickets == 0 ? 0.0 : (totalTickets - breachedCount) * 100.0 / totalTickets;
        double avgResolutionTimeHours = averageResolutionTimeHours(ticketRepository.findByResolutionTimeHoursIsNotNull());

        return new RegionalKpiSummaryDto(
                totalTickets,
                activeCities,
                round2(successRate),
                round2(avgResolutionTimeHours)
        );
    }

    private List<RegionDensityDto> buildRegionDensity(List<NamedResolutionStat> geographicalRegionStats) {
        long maxTicketCount = geographicalRegionStats.stream()
                .mapToLong(NamedResolutionStat::ticketCount)
                .max()
                .orElse(0);

        return geographicalRegionStats.stream()
                .map(stat -> new RegionDensityDto(
                        stat.name(),
                        stat.ticketCount(),
                        round2(stat.avgResolutionTimeHours()),
                        classifyDensity(stat.ticketCount(), maxTicketCount)
                ))
                .toList();
    }

    private List<CityDensityDto> buildCityDensity(List<NamedResolutionStat> cityStats) {
        long maxTicketCount = cityStats.stream()
                .mapToLong(NamedResolutionStat::ticketCount)
                .max()
                .orElse(0);

        List<NamedResolutionStat> sorted = cityStats.stream()
                .sorted(Comparator.comparingLong(NamedResolutionStat::ticketCount).reversed())
                .toList();

        return IntStream.range(0, sorted.size())
                .mapToObj(i -> {
                    NamedResolutionStat stat = sorted.get(i);
                    return new CityDensityDto(
                            i + 1,
                            stat.name(),
                            stat.ticketCount(),
                            round2(stat.avgResolutionTimeHours()),
                            classifyDensity(stat.ticketCount(), maxTicketCount)
                    );
                })
                .toList();
    }

    private String classifyDensity(long ticketCount, long maxTicketCount) {
        if (ticketCount == 0 || maxTicketCount == 0) {
            return "LOW";
        }
        double ratio = (double) ticketCount / maxTicketCount;
        if (ratio >= HIGH_DENSITY_RATIO) {
            return "HIGH";
        }
        if (ratio >= MEDIUM_DENSITY_RATIO) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private static double averageResolutionTimeHours(List<ResolutionTimeProjection> projections) {
        return projections.stream()
                .mapToDouble(p -> p.getResolutionTimeHours().doubleValue())
                .average()
                .orElse(0.0);
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static long orZero(Long value) {
        return value != null ? value : 0L;
    }

    private record NamedResolutionStat(String name, long ticketCount, double avgResolutionTimeHours) {
    }
}
