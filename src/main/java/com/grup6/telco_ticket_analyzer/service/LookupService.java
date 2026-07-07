package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.LookupDto;
import com.grup6.telco_ticket_analyzer.model.Agent;
import com.grup6.telco_ticket_analyzer.model.Department;
import com.grup6.telco_ticket_analyzer.model.InfrastructureType;
import com.grup6.telco_ticket_analyzer.model.IssueTopic;
import com.grup6.telco_ticket_analyzer.model.Region;
import com.grup6.telco_ticket_analyzer.model.ServiceType;
import com.grup6.telco_ticket_analyzer.repository.AgentRepository;
import com.grup6.telco_ticket_analyzer.repository.DepartmentRepository;
import com.grup6.telco_ticket_analyzer.repository.InfrastructureTypeRepository;
import com.grup6.telco_ticket_analyzer.repository.IssueTopicRepository;
import com.grup6.telco_ticket_analyzer.repository.RegionRepository;
import com.grup6.telco_ticket_analyzer.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LookupService {

    private final DepartmentRepository departmentRepository;
    private final IssueTopicRepository issueTopicRepository;
    private final RegionRepository regionRepository;
    private final AgentRepository agentRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final InfrastructureTypeRepository infrastructureTypeRepository;
    private final CustomerRepository customerRepository;

    public List<LookupDto> getDepartments() {
        return sorted(departmentRepository.findAll().stream()
                .map(department -> new LookupDto(department.getId(), department.getDepartmentName())));
    }

    public List<LookupDto> getIssueTopics() {
        return sorted(issueTopicRepository.findAll().stream()
                .map(topic -> new LookupDto(topic.getId(), topic.getTopicName())));
    }

    public List<LookupDto> getRegions() {
        return sorted(regionRepository.findAll().stream()
                .map(region -> new LookupDto(region.getId(), region.getCityName())));
    }

    public List<LookupDto> getServiceTypes() {
        return sorted(serviceTypeRepository.findAll().stream()
                .map(serviceType -> new LookupDto(serviceType.getId(), serviceType.getServiceName())));
    }

    public List<LookupDto> getInfrastructureTypes() {
        return sorted(infrastructureTypeRepository.findAll().stream()
                .map(infrastructureType -> new LookupDto(infrastructureType.getId(), infrastructureType.getInfrastructureName())));
    }

    public List<LookupDto> getAgents(UUID departmentId) {
        List<Agent> agents = departmentId != null
                ? agentRepository.findByDepartmentId(departmentId)
                : agentRepository.findAll();

        return sorted(agents.stream()
                .map(agent -> new LookupDto(agent.getId(), agent.getFullName())));
    }

    public List<LookupDto> getCustomerSegments() {
        return customerRepository.findAll()
                .stream()
                .map(customer -> customer.getSegment())
                .filter(segment -> segment != null && !segment.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .map(segment -> new LookupDto(null, segment))
                .toList();
    }

    public List<LookupDto> getRiskLevels() {
        return Arrays.stream(RiskLevel.values())
                .map(riskLevel -> new LookupDto(null, riskLevel.name()))
                .toList();
    }

    private List<LookupDto> sorted(java.util.stream.Stream<LookupDto> stream) {
        return stream
                .sorted(Comparator.comparing(LookupDto::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
