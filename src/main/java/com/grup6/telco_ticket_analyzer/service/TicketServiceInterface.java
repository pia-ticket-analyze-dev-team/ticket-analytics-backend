package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TicketServiceInterface {

    PagedResponseDto<TicketResponseDto> getAllTickets(
            int page,
            String status,
            String priority,
            UUID topicId,
            UUID departmentId,
            UUID regionId,
            Boolean slaBreached,
            UUID agentId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    TicketResponseDto getTicketById(UUID id);

    TicketResponseDto createTicket(TicketRequestDto requestDto);

    TicketResponseDto updateTicket(UUID id, TicketRequestDto requestDto);

    void deleteTicket(UUID id);
}