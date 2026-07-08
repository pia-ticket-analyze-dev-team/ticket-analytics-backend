package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketCreateDto;
import com.grup6.telco_ticket_analyzer.dto.TicketForwardRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TicketServiceInterface {

    PagedResponseDto<TicketResponseDto> getAllTickets(
            int page,
            int size,
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

    PagedResponseDto<TicketResponseDto> getTicketsByCustomerId(UUID customerId, int page, int size);

    PagedResponseDto<TicketResponseDto> getTicketsByAgentId(UUID agentId, int page, int size);

    TicketResponseDto createTicket(TicketCreateDto requestDto);

    TicketResponseDto updateTicket(UUID id, TicketRequestDto requestDto);

    TicketResponseDto forwardTicket(UUID ticketId, TicketForwardRequestDto requestDto);

    void deleteTicket(UUID id);
}