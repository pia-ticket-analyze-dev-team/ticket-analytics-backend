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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceTypeTrendService {

    private final TicketRepository ticketRepository;

    public List<ServiceTypeTrendDto> getServiceTypeTrend() {
        YearMonth currentMonth = YearMonth.now();

        LocalDateTime startDate = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<ServiceTypeTrendProjection> tickets =
                ticketRepository.findByServiceTypeIsNotNullAndCreatedAtBetween(startDate, endDate);

        return tickets.stream()
                .collect(Collectors.groupingBy(ticket -> ticket.getServiceType().getId()))
                .entrySet()
                .stream()
                .map(entry -> toDto(entry.getKey(), currentMonth, entry.getValue()))
                .sorted(Comparator.comparing(ServiceTypeTrendDto::ticketCount).reversed())
                .limit(5)
                .toList();
    }

    private ServiceTypeTrendDto toDto(
            UUID serviceTypeId,
            YearMonth month,
            List<ServiceTypeTrendProjection> tickets
    ) {
        ServiceTypeInfoProjection serviceType = tickets.get(0).getServiceType();

        return new ServiceTypeTrendDto(
                serviceTypeId,
                serviceType.getServiceName(),
                month,
                tickets.size()
        );
    }
}