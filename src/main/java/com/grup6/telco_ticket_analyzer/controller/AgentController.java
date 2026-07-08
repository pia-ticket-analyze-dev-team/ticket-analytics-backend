package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;
import com.grup6.telco_ticket_analyzer.service.TicketServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
@Tag(name = "Temsilciler (Agent)", description = "Temsilcilere atanmış ticket'ları sorgulayan uçlar")
public class AgentController {

    private final TicketServiceInterface ticketService;

    @Operation(summary = "Temsilcinin ticket'larını listele", description = "Belirtilen temsilciye atanmış ticket'ları duruma ve önceliğe göre filtrelenmiş, sayfalanmış şekilde döner.")
    @GetMapping("/{id}/tickets")
    public PagedResponseDto<TicketResponseDto> getAgentTickets(
            @PathVariable UUID id,
            @Parameter(description = "Sayfa numarası (0'dan başlar)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa başına kayıt sayısı") @RequestParam(defaultValue = "50") int size,
            @Parameter(description = "Ticket durumuna göre filtre") @RequestParam(required = false) String status,
            @Parameter(description = "Ticket önceliğine göre filtre") @RequestParam(required = false) String priority) {
        return ticketService.getTicketsByAgentId(id, page, size, status, priority);
    }
}
