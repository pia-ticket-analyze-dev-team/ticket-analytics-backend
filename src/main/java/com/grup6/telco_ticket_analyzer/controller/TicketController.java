package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;
import com.grup6.telco_ticket_analyzer.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public PagedResponseDto<TicketResponseDto> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ticketService.getAllTickets(page, size);
    }

    @GetMapping("/{id}")
    public TicketResponseDto getTicketById(@PathVariable UUID id) {
        return ticketService.getTicketById(id);
    }

    @PostMapping
    public TicketResponseDto createTicket(@RequestBody TicketRequestDto requestDto) {
        return ticketService.createTicket(requestDto);
    }

    @PutMapping("/{id}")
    public TicketResponseDto updateTicket(
            @PathVariable UUID id,
            @RequestBody TicketRequestDto requestDto
    ) {
        return ticketService.updateTicket(id, requestDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
    }
}