package com.grup6.telco_ticket_analyzer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponseDto(
        UUID id,
        String ticketNumber,
        UUID customerId,
        String customerName,
        UUID topicId,
        String issueTopicName,
        UUID currentDepartmentId,
        String departmentName,
        UUID regionId,
        String city,
        UUID agentId,
        String assignedAgentName,
        UUID serviceTypeId,
        String serviceTypeName,
        UUID infrastructureTypeId,
        String infrastructureTypeName,
        String description,
        String status,
        String priority,
        boolean slaBreached,
        BigDecimal resolutionTimeHours,
        Integer customerSatisfactionScore,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt,
        String creationSource,
        Integer slaTargetHours,
        String slaStatus,
        Long slaHoursRemaining
) {
}