package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.TicketDepartmentHistoryDto;
import com.grup6.telco_ticket_analyzer.service.TicketHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketHistoryController {

    private final TicketHistoryService ticketHistoryService;

    @GetMapping("/{ticketId}/department-history")
    public List<TicketDepartmentHistoryDto> getTicketDepartmentHistory(
            @PathVariable UUID ticketId
    ) {
        return ticketHistoryService.getDepartmentHistoryByTicketId(ticketId);
    }
}