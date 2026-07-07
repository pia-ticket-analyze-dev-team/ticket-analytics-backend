package com.grup6.telco_ticket_analyzer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketCreateDto(
        UUID customerId,
        NewCustomerDto newCustomer,
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
        LocalDateTime resolvedAt
) {
    public TicketCreateDto {
        if ((customerId != null) == (newCustomer != null)) {
            throw new IllegalArgumentException("Exactly one of customerId or newCustomer must be provided");
        }
    }
}
