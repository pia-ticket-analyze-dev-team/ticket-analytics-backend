package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketCreateDto;
import com.grup6.telco_ticket_analyzer.dto.TicketForwardRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;
import com.grup6.telco_ticket_analyzer.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Ticket'lar", description = "Destek taleplerinin (ticket) oluşturulması, listelenmesi, güncellenmesi ve yönlendirilmesiyle ilgili uçlar")
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Ticket'ları listele", description = "Duruma, önceliğe, konuya, departmana, bölgeye, SLA ihlaline, temsilciye ve tarih aralığına göre filtrelenebilen sayfalanmış ticket listesini döner.")
    @GetMapping
    public PagedResponseDto<TicketResponseDto> getAllTickets(
            @Parameter(description = "Sayfa numarası (0'dan başlar)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa başına kayıt sayısı") @RequestParam(defaultValue = "50") int size,
            @Parameter(description = "Ticket durumuna göre filtre (örn. OPEN, CLOSED)") @RequestParam(required = false) String status,
            @Parameter(description = "Ticket önceliğine göre filtre (örn. LOW, HIGH)") @RequestParam(required = false) String priority,
            @Parameter(description = "Konu (topic) ID'sine göre filtre") @RequestParam(required = false) UUID topicId,
            @Parameter(description = "Departman ID'sine göre filtre") @RequestParam(required = false) UUID departmentId,
            @Parameter(description = "Bölge ID'sine göre filtre") @RequestParam(required = false) UUID regionId,
            @Parameter(description = "SLA süresi ihlal edilmiş ticket'lara göre filtre") @RequestParam(required = false) Boolean slaBreached,
            @Parameter(description = "Atanan temsilci (agent) ID'sine göre filtre") @RequestParam(required = false) UUID agentId,
            @Parameter(description = "Bu tarihten (dahil) itibaren oluşturulan ticket'lar") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Bu tarihe (dahil) kadar oluşturulan ticket'lar") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
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

    @Operation(summary = "ID'ye göre ticket getir", description = "Belirtilen UUID'ye sahip ticket'ın detay bilgilerini döner.")
    @GetMapping("/{id}")
    public TicketResponseDto getTicketById(@PathVariable UUID id) {
        return ticketService.getTicketById(id);
    }

    @Operation(summary = "Yeni ticket oluştur", description = "Verilen bilgilerle yeni bir destek talebi (ticket) oluşturur ve oluşturulan ticket'ı döner.")
    @PostMapping
    public ResponseEntity<TicketResponseDto> createTicket(@RequestBody TicketCreateDto requestDto) {
        TicketResponseDto created = ticketService.createTicket(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Ticket'ı güncelle", description = "Belirtilen ID'ye sahip ticket'ın bilgilerini verilen değerlerle günceller.")
    @PutMapping("/{id}")
    public TicketResponseDto updateTicket(
            @PathVariable UUID id,
            @RequestBody TicketRequestDto requestDto) {
        return ticketService.updateTicket(id, requestDto);
    }

    @Operation(summary = "Ticket'ı yönlendir", description = "Belirtilen ticket'ı başka bir departman veya temsilciye yönlendirir.")
    @PostMapping("/{id}/forward")
    public TicketResponseDto forwardTicket(
            @PathVariable UUID id,
            @RequestBody TicketForwardRequestDto requestDto) {
        return ticketService.forwardTicket(id, requestDto);
    }

    @Operation(summary = "Ticket'ı sil", description = "Belirtilen ID'ye sahip ticket kaydını siler.")
    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
    }
}