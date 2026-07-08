package com.grup6.telco_ticket_analyzer.service.analytics;

import com.grup6.telco_ticket_analyzer.dto.analytics.CustomerChurnRiskDto;
import com.grup6.telco_ticket_analyzer.repository.TicketRepository;
import com.grup6.telco_ticket_analyzer.repository.projection.CustomerChurnProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.CustomerInfoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.grup6.telco_ticket_analyzer.model.enums.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@Service
@RequiredArgsConstructor
public class CustomerChurnRiskService {

    private final TicketRepository ticketRepository;

public Page<CustomerChurnRiskDto> getCustomerChurnRisk(int page, int size, String segment, String riskLevel) {
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusDays(90);

    List<CustomerChurnProjection> tickets =
            ticketRepository.findByCustomerIsNotNullAndCreatedAtBetween(startDate, endDate);

    List<CustomerChurnRiskDto> allCustomers = tickets.stream()
            .collect(Collectors.groupingBy(ticket -> ticket.getCustomer().getId()))
            .entrySet()
            .stream()
            .map(entry -> toDto(entry.getKey(), entry.getValue()))
            .filter(customer -> segment == null || segment.isBlank()
            || customer.customerSegment().equalsIgnoreCase(segment))
            .filter(customer -> riskLevel == null || riskLevel.isBlank()
            || customer.riskLevel().equalsIgnoreCase(riskLevel))
            .sorted(Comparator.comparing(CustomerChurnRiskDto::churnRiskScore).reversed())
            .toList();

    int start = page * size;
    int end = Math.min(start + size, allCustomers.size());

    List<CustomerChurnRiskDto> pagedContent = start >= allCustomers.size()
            ? List.of()
            : allCustomers.subList(start, end);

    return new PageImpl<>(
            pagedContent,
            PageRequest.of(page, size),
            allCustomers.size()
    );
}
    private CustomerChurnRiskDto toDto(UUID customerId, List<CustomerChurnProjection> tickets) {
        CustomerInfoProjection customer = tickets.get(0).getCustomer();

        long ticketCount = tickets.size();
        double averageSatisfaction = calculateAverageSatisfaction(tickets);
        double slaBreachRate = calculateSlaBreachRate(tickets);
        double averageResolutionHours = calculateAverageResolutionHours(tickets);

        double churnRiskScore =
                calculateTicketVolumeRisk(ticketCount) * 0.20
                        + calculateSatisfactionRisk(averageSatisfaction) * 0.35
                        + slaBreachRate * 0.30
                        + calculateResolutionRisk(averageResolutionHours) * 0.15;

        churnRiskScore = round(churnRiskScore);

        return new CustomerChurnRiskDto(
                customerId,
                customer.getFirstName() + " " + customer.getLastName(),
                customer.getSegment(),
                ticketCount,
                round(averageSatisfaction),
                round(slaBreachRate),
                round(averageResolutionHours),
                churnRiskScore,
                getRiskLevel(churnRiskScore)
        );
    }

    private double calculateAverageSatisfaction(List<CustomerChurnProjection> tickets) {
        return tickets.stream()
                .map(CustomerChurnProjection::getCustomerSatisfactionScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    private double calculateSlaBreachRate(List<CustomerChurnProjection> tickets) {
        if (tickets.isEmpty()) {
            return 0;
        }

        long breachedCount = tickets.stream()
                .filter(CustomerChurnProjection::isSlaBreached)
                .count();

        return breachedCount * 100.0 / tickets.size();
    }

    private double calculateAverageResolutionHours(List<CustomerChurnProjection> tickets) {
        return tickets.stream()
                .map(CustomerChurnProjection::getResolutionTimeHours)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0);
    }

    private double calculateTicketVolumeRisk(long ticketCount) {
        return Math.min(ticketCount * 5.0, 100);
    }

    private double calculateSatisfactionRisk(double averageSatisfaction) {
        if (averageSatisfaction == 0) {
            return 50;
        }

        return Math.max(0, ((5 - averageSatisfaction) / 4) * 100);
    }

    private double calculateResolutionRisk(double averageResolutionHours) {
        return Math.min(averageResolutionHours * 5, 100);
    }

   private String getRiskLevel(double churnRiskScore) {
    if (churnRiskScore >= 70) {
        return RiskLevel.HIGH.name();
    }

    if (churnRiskScore >= 40) {
        return RiskLevel.MEDIUM.name();
    }

    return RiskLevel.LOW.name();
}

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}