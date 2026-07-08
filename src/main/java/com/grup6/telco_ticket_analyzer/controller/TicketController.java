package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketCreateDto;
import com.grup6.telco_ticket_analyzer.dto.TicketForwardRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;
import com.grup6.telco_ticket_analyzer.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) UUID topicId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID regionId,
            @RequestParam(required = false) Boolean slaBreached,
            @RequestParam(required = false) UUID agentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ticketService.getAllTickets(
                page,
                size,
                status,
                priority,
                topicId,
                departmentId,
                regionId,
                slaBreached,
                agentId,
                startDate,
                endDate);
    }

    @GetMapping("/{id}")
    public TicketResponseDto getTicketById(@PathVariable UUID id) {
        return ticketService.getTicketById(id);
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> createTicket(@RequestBody TicketCreateDto requestDto) {
        TicketResponseDto created = ticketService.createTicket(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public TicketResponseDto updateTicket(
            @PathVariable UUID id,
            @RequestBody TicketRequestDto requestDto) {
        return ticketService.updateTicket(id, requestDto);
    }

    @PostMapping("/{id}/forward")
    public TicketResponseDto forwardTicket(
            @PathVariable UUID id,
            @RequestBody TicketForwardRequestDto requestDto) {
        return ticketService.forwardTicket(id, requestDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
    }
}