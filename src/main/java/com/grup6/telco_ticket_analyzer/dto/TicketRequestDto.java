package com.grup6.telco_ticket_analyzer.dto;

public record TicketRequestDto(
        String customerName,
        String issueTopicName,
        String departmentName,
        String cityName,
        String serviceTypeName,
        String infrastructureTypeName,
        String priority,
        String status,
        String assignedAgentName,
        String description,
        String creationSource
) {
}