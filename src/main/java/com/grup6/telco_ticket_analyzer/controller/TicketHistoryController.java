package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.TicketDepartmentHistoryDto;
import com.grup6.telco_ticket_analyzer.service.TicketHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket Geçmişi", description = "Ticket'ların departmanlar arası geçmişini gösteren uçlar")
public class TicketHistoryController {

    private final TicketHistoryService ticketHistoryService;

    @Operation(summary = "Ticket'ın departman geçmişini getir", description = "Belirtilen ticket'ın hangi departmanlara hangi sırayla yönlendirildiğini gösteren geçmiş kayıtlarını döner.")
    @GetMapping("/{ticketId}/department-history")
    public List<TicketDepartmentHistoryDto> getTicketDepartmentHistory(
            @PathVariable UUID ticketId
    ) {
        return ticketHistoryService.getDepartmentHistoryByTicketId(ticketId);
    }
}