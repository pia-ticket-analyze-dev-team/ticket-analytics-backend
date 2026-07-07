package com.grup6.telco_ticket_analyzer.service.analytics;

import com.grup6.telco_ticket_analyzer.dto.analytics.AgentPerformanceDto;
import com.grup6.telco_ticket_analyzer.repository.TicketRepository;
import com.grup6.telco_ticket_analyzer.repository.projection.AgentInfoProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.AgentPerformanceProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentPerformanceService {

    private final TicketRepository ticketRepository;

public Page<AgentPerformanceDto> getAgentPerformance(int page, int size) {
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusDays(30);

    List<AgentPerformanceProjection> tickets =
            ticketRepository.findByAgentIsNotNullAndResolvedAtBetween(startDate, endDate);

    List<AgentPerformanceDto> allAgentPerformances = tickets.stream()
            .collect(Collectors.groupingBy(ticket -> ticket.getAgent().getId()))
            .entrySet()
            .stream()
            .map(entry -> toDto(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(AgentPerformanceDto::performanceScore).reversed())
            .toList();

    int start = page * size;
    int end = Math.min(start + size, allAgentPerformances.size());

    List<AgentPerformanceDto> pagedContent = start >= allAgentPerformances.size()
            ? List.of()
            : allAgentPerformances.subList(start, end);

    return new PageImpl<>(
            pagedContent,
            PageRequest.of(page, size),
            allAgentPerformances.size()
    );
}

    private AgentPerformanceDto toDto(UUID agentId, List<AgentPerformanceProjection> tickets) {
        AgentInfoProjection agent = tickets.get(0).getAgent();

        long resolvedTicketCount = tickets.size();
        double averageSatisfaction = calculateAverageSatisfaction(tickets);
        double slaSuccessRate = 100 - calculateSlaBreachRate(tickets);
        double averageResolutionHours = calculateAverageResolutionHours(tickets);

        double performanceScore =
                calculateResolvedTicketScore(resolvedTicketCount) * 0.25
                        + calculateSatisfactionScore(averageSatisfaction) * 0.35
                        + slaSuccessRate * 0.25
                        + calculateFastResolutionScore(averageResolutionHours) * 0.15;

        return new AgentPerformanceDto(
                agentId,
                agent.getFullName(),
                resolvedTicketCount,
                round(averageSatisfaction),
                round(slaSuccessRate),
                round(averageResolutionHours),
                round(performanceScore)
        );
    }

    private double calculateAverageSatisfaction(List<AgentPerformanceProjection> tickets) {
        return tickets.stream()
                .map(AgentPerformanceProjection::getCustomerSatisfactionScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    private double calculateSlaBreachRate(List<AgentPerformanceProjection> tickets) {
        if (tickets.isEmpty()) {
            return 0;
        }

        long breachedCount = tickets.stream()
                .filter(AgentPerformanceProjection::isSlaBreached)
                .count();

        return breachedCount * 100.0 / tickets.size();
    }

    private double calculateAverageResolutionHours(List<AgentPerformanceProjection> tickets) {
        return tickets.stream()
                .map(AgentPerformanceProjection::getResolutionTimeHours)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0);
    }

    private double calculateResolvedTicketScore(long resolvedTicketCount) {
        return Math.min(resolvedTicketCount * 5.0, 100);
    }

    private double calculateSatisfactionScore(double averageSatisfaction) {
        if (averageSatisfaction == 0) {
            return 0;
        }

        return Math.min((averageSatisfaction / 5.0) * 100, 100);
    }

    private double calculateFastResolutionScore(double averageResolutionHours) {
        if (averageResolutionHours == 0) {
            return 0;
        }

        return Math.max(0, 100 - averageResolutionHours * 4);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}