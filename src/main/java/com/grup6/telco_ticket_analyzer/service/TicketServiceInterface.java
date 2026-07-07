package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;

import java.util.UUID;

public interface TicketServiceInterface {

    PagedResponseDto<TicketResponseDto> getAllTickets(int page, int size);

    TicketResponseDto getTicketById(UUID id);

    TicketResponseDto createTicket(TicketRequestDto requestDto);

    TicketResponseDto updateTicket(UUID id, TicketRequestDto requestDto);

    void deleteTicket(UUID id);
}