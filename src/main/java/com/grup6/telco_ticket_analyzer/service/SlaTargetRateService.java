package com.grup6.telco_ticket_analyzer.service.analytics;

import com.grup6.telco_ticket_analyzer.dto.analytics.SlaTargetRateDto;
import com.grup6.telco_ticket_analyzer.repository.TicketRepository;
import com.grup6.telco_ticket_analyzer.repository.projection.SlaTargetProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlaTargetRateService {

    private final TicketRepository ticketRepository;

    public SlaTargetRateDto getSlaTargetRate() {
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusDays(30);

    List<SlaTargetProjection> tickets =
            ticketRepository.findByCreatedAtBetween(startDate, endDate);

    long totalTicketCount = tickets.size();

    long breachedTicketCount = tickets.stream()
            .filter(SlaTargetProjection::isSlaBreached)
            .count();

    long withinSlaTicketCount = totalTicketCount - breachedTicketCount;

    double slaTargetRate = totalTicketCount == 0
            ? 0
            : withinSlaTicketCount * 100.0 / totalTicketCount;

    return new SlaTargetRateDto(
            totalTicketCount,
            breachedTicketCount,
            withinSlaTicketCount,
            round(slaTargetRate)
    );
}

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}