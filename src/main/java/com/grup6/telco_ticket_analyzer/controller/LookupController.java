package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.LookupDto;
import com.grup6.telco_ticket_analyzer.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LookupController {

    private final LookupService lookupService;

    @GetMapping("/api/departments")
    public List<LookupDto> getDepartments() {
        return lookupService.getDepartments();
    }

    @GetMapping("/api/topics")
    public List<LookupDto> getIssueTopics() {
        return lookupService.getIssueTopics();
    }

    @GetMapping("/api/regions")
    public List<LookupDto> getRegions() {
        return lookupService.getRegions();
    }

    @GetMapping("/api/service-types")
    public List<LookupDto> getServiceTypes() {
        return lookupService.getServiceTypes();
    }

    @GetMapping("/api/infrastructure-types")
    public List<LookupDto> getInfrastructureTypes() {
        return lookupService.getInfrastructureTypes();
    }

    @GetMapping("/api/agents")
    public List<LookupDto> getAgents(@RequestParam(required = false) UUID departmentId) {
        return lookupService.getAgents(departmentId);
    }

     @GetMapping("/api/customer-segments")
    public List<LookupDto> getCustomerSegments() {
        return lookupService.getCustomerSegments();
    }

    @GetMapping("/api/risk-levels")
    public List<LookupDto> getRiskLevels() {
        return lookupService.getRiskLevels();
    }
}

