package com.grup6.telco_ticket_analyzer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketRequestDto(
        String ticketNumber,
        UUID customerId,
        UUID topicId,
        UUID currentDepartmentId,
        UUID agentId,
        UUID regionId,
        UUID serviceTypeId,
        UUID infrastructureTypeId,
        String description,
        String status,
        String priority,
        boolean slaBreached,
        BigDecimal resolutionTimeHours,
        Integer customerSatisfactionScore,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt,
        String creationSource
) {
}