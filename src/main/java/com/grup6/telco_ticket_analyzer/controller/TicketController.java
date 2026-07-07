package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;
import com.grup6.telco_ticket_analyzer.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public PagedResponseDto<TicketResponseDto> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) UUID topicId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID regionId,
            @RequestParam(required = false) Boolean slaBreached,
            @RequestParam(required = false) UUID agentId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate
    ) {
        return ticketService.getAllTickets(
                page,
                status,
                priority,
                topicId,
                departmentId,
                regionId,
                slaBreached,
                agentId,
                startDate,
                endDate
        );
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