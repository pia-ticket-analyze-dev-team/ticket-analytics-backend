package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.LookupDto;
import com.grup6.telco_ticket_analyzer.service.LookupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Sabit Listeler (Lookup)", description = "Formlarda seçim kutuları (dropdown) için kullanılan sabit/referans veri listelerini sağlayan uçlar")
public class LookupController {

    private final LookupService lookupService;

    @Operation(summary = "Departman listesi", description = "Sistemdeki tüm departmanların listesini döner.")
    @GetMapping("/api/departments")
    public List<LookupDto> getDepartments() {
        return lookupService.getDepartments();
    }

    @Operation(summary = "Konu (topic) listesi", description = "Ticket açarken seçilebilecek konu başlıklarının listesini döner.")
    @GetMapping("/api/topics")
    public List<LookupDto> getIssueTopics() {
        return lookupService.getIssueTopics();
    }

    @Operation(summary = "Bölge listesi", description = "Sistemdeki tüm bölgelerin listesini döner.")
    @GetMapping("/api/regions")
    public List<LookupDto> getRegions() {
        return lookupService.getRegions();
    }

    @Operation(summary = "Servis tipi listesi", description = "Sistemdeki tüm servis tiplerinin listesini döner.")
    @GetMapping("/api/service-types")
    public List<LookupDto> getServiceTypes() {
        return lookupService.getServiceTypes();
    }

    @Operation(summary = "Altyapı tipi listesi", description = "Sistemdeki tüm altyapı tiplerinin listesini döner.")
    @GetMapping("/api/infrastructure-types")
    public List<LookupDto> getInfrastructureTypes() {
        return lookupService.getInfrastructureTypes();
    }

    @Operation(summary = "Temsilci (agent) listesi", description = "İsteğe bağlı olarak departmana göre filtrelenebilen temsilci listesini döner.")
    @GetMapping("/api/agents")
    public List<LookupDto> getAgents(
            @Parameter(description = "Departman ID'sine göre filtre") @RequestParam(required = false) UUID departmentId) {
        return lookupService.getAgents(departmentId);
    }

    @Operation(summary = "Müşteri segmenti listesi", description = "Sistemdeki tüm müşteri segmentlerinin listesini döner.")
    @GetMapping("/api/customer-segments")
    public List<LookupDto> getCustomerSegments() {
        return lookupService.getCustomerSegments();
    }

    @Operation(summary = "Risk seviyesi listesi", description = "Müşteri kayıp riski (churn) için kullanılan risk seviyelerinin listesini döner.")
    @GetMapping("/api/risk-levels")
    public List<LookupDto> getRiskLevels() {
        return lookupService.getRiskLevels();
    }
}

