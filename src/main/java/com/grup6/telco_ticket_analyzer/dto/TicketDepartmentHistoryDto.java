package com.grup6.telco_ticket_analyzer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketDepartmentHistoryDto(
        UUID historyId,
        UUID ticketId,
        UUID previousDepartmentId,
        String previousDepartmentName,
        UUID newDepartmentId,
        String newDepartmentName,
        UUID agentId,
        String agentName,
        String previousStatus,
        String newStatus,
        String actionType,
        LocalDateTime changedAt,
        Integer durationMinutes
) {
}