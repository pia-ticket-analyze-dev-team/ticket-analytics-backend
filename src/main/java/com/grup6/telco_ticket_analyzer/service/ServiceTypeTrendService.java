package com.grup6.telco_ticket_analyzer.service.analytics;

import com.grup6.telco_ticket_analyzer.dto.analytics.ServiceTypeTrendDto;
import com.grup6.telco_ticket_analyzer.repository.TicketRepository;
import com.grup6.telco_ticket_analyzer.repository.projection.ServiceTypeInfoProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.ServiceTypeTrendProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceTypeTrendService {

    private final TicketRepository ticketRepository;

    public List<ServiceTypeTrendDto> getServiceTypeTrend() {
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusMonths(6);

    List<ServiceTypeTrendProjection> tickets =
            ticketRepository.findByServiceTypeIsNotNullAndCreatedAtBetween(startDate, endDate);

    Map<YearMonth, Long> totalTicketCountByMonth = tickets.stream()
            .collect(Collectors.groupingBy(
                    ticket -> YearMonth.from(ticket.getCreatedAt()),
                    Collectors.counting()
            ));

    return tickets.stream()
            .collect(Collectors.groupingBy(
                    ticket -> ticket.getServiceType().getId(),
                    Collectors.groupingBy(
                            ticket -> YearMonth.from(ticket.getCreatedAt()),
                            Collectors.toList()
                    )
            ))
            .entrySet()
            .stream()
            .flatMap(serviceEntry -> serviceEntry.getValue().entrySet().stream()
                    .map(monthEntry -> toDto(
                            serviceEntry.getKey(),
                            monthEntry.getKey(),
                            monthEntry.getValue(),
                            totalTicketCountByMonth
                    ))
            )
            .sorted(Comparator
                    .comparing(ServiceTypeTrendDto::month)
                    .thenComparing(ServiceTypeTrendDto::serviceTypeName))
            .toList();
}

    private ServiceTypeTrendDto toDto(
            UUID serviceTypeId,
            YearMonth month,
            List<ServiceTypeTrendProjection> tickets,
            Map<YearMonth, Long> totalTicketCountByMonth
    ) {
        ServiceTypeInfoProjection serviceType = tickets.get(0).getServiceType();

        long ticketCount = tickets.size();
        long monthlyTotal = totalTicketCountByMonth.getOrDefault(month, 0L);

        double percentage = monthlyTotal == 0
                ? 0
                : ticketCount * 100.0 / monthlyTotal;

        return new ServiceTypeTrendDto(
                serviceTypeId,
                serviceType.getServiceName(),
                month,
                ticketCount,
                round(percentage)
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}