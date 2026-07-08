package com.grup6.telco_ticket_analyzer.dto;

import java.util.UUID;

public record TicketForwardRequestDto(
        UUID agentId,
        UUID targetDepartmentId
) {
}
