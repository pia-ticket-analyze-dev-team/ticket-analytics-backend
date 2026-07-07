package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;
import com.grup6.telco_ticket_analyzer.exception.TicketNotFoundException;
import com.grup6.telco_ticket_analyzer.model.*;
import com.grup6.telco_ticket_analyzer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService implements TicketServiceInterface {

    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final int MAX_PAGE_SIZE = 50;

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final IssueTopicRepository issueTopicRepository;
    private final DepartmentRepository departmentRepository;
    private final AgentRepository agentRepository;
    private final RegionRepository regionRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final InfrastructureTypeRepository infrastructureTypeRepository;

    @Override
    public PagedResponseDto<TicketResponseDto> getAllTickets(int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                clampSize(size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Ticket> ticketPage = ticketRepository.findAll(pageable);

        List<TicketResponseDto> content = ticketPage.getContent()
                .stream()
                .map(this::toResponseDto)
                .toList();

        return new PagedResponseDto<>(
                content,
                ticketPage.getNumber(),
                ticketPage.getSize(),
                ticketPage.getTotalElements(),
                ticketPage.getTotalPages()
        );
    }

    @Override
    public TicketResponseDto getTicketById(UUID id) {
        return toResponseDto(findTicketOrThrow(id));
    }

    @Override
    public TicketResponseDto createTicket(TicketRequestDto requestDto) {
        Department department = findDepartmentByName(requestDto.departmentName());

        Ticket ticket = new Ticket();
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setCustomer(findCustomerByFullName(requestDto.customerName()));
        ticket.setTopic(findIssueTopicByName(requestDto.issueTopicName()));
        ticket.setCurrentDepartment(department);
        ticket.setRegion(findRegionByCityName(requestDto.cityName()));
        ticket.setServiceType(findServiceTypeByName(requestDto.serviceTypeName()));
        ticket.setInfrastructureType(findInfrastructureTypeByName(requestDto.infrastructureTypeName()));
        ticket.setAgent(findAgent(requestDto.assignedAgentName(), department));

        ticket.setDescription(requestDto.description());
        ticket.setPriority(requestDto.priority());
        ticket.setStatus(resolveStatus(requestDto.status()));
        ticket.setCreationSource(requestDto.creationSource());

        ticket.setSlaBreached(false);
        ticket.setResolutionTimeHours(null);
        ticket.setCustomerSatisfactionScore(null);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setResolvedAt(null);

        return toResponseDto(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponseDto updateTicket(UUID id, TicketRequestDto requestDto) {
        Ticket ticket = findTicketOrThrow(id);
        Department department = findDepartmentByName(requestDto.departmentName());

        ticket.setCustomer(findCustomerByFullName(requestDto.customerName()));
        ticket.setTopic(findIssueTopicByName(requestDto.issueTopicName()));
        ticket.setCurrentDepartment(department);
        ticket.setRegion(findRegionByCityName(requestDto.cityName()));
        ticket.setServiceType(findServiceTypeByName(requestDto.serviceTypeName()));
        ticket.setInfrastructureType(findInfrastructureTypeByName(requestDto.infrastructureTypeName()));
        ticket.setAgent(findAgent(requestDto.assignedAgentName(), department));

        ticket.setDescription(requestDto.description());
        ticket.setPriority(requestDto.priority());
        ticket.setStatus(resolveStatus(requestDto.status()));
        ticket.setCreationSource(requestDto.creationSource());

        if ("RESOLVED".equalsIgnoreCase(ticket.getStatus()) || "CLOSED".equalsIgnoreCase(ticket.getStatus())) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        return toResponseDto(ticketRepository.save(ticket));
    }

    @Override
    public void deleteTicket(UUID id) {
        ticketRepository.delete(findTicketOrThrow(id));
    }

    private int clampSize(int size) {
        if (size < 1) {
            return DEFAULT_PAGE_SIZE;
        }

        return Math.min(size, MAX_PAGE_SIZE);
    }

    private Ticket findTicketOrThrow(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    private String generateTicketNumber() {
        return "TCKT-" + LocalDateTime.now().getYear() + "-" + System.currentTimeMillis();
    }

    private String resolveStatus(String status) {
        if (status == null || status.isBlank()) {
            return "OPEN";
        }

        return status;
    }

    private Customer findCustomerByFullName(String customerName) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }

        String[] parts = customerName.trim().split("\\s+");

        if (parts.length < 2) {
            throw new IllegalArgumentException("Customer name must include first name and last name");
        }

        String lastName = parts[parts.length - 1];
        String firstName = customerName.substring(0, customerName.lastIndexOf(lastName)).trim();

        return customerRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerName));
    }

    private IssueTopic findIssueTopicByName(String issueTopicName) {
        return issueTopicRepository.findByTopicName(issueTopicName)
                .orElseThrow(() -> new IllegalArgumentException("Issue topic not found: " + issueTopicName));
    }

    private Department findDepartmentByName(String departmentName) {
        return departmentRepository.findByDepartmentName(departmentName)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentName));
    }

    private Region findRegionByCityName(String cityName) {
        return regionRepository.findByCityName(cityName)
                .orElseThrow(() -> new IllegalArgumentException("City not found: " + cityName));
    }

    private ServiceType findServiceTypeByName(String serviceTypeName) {
        return serviceTypeRepository.findByServiceName(serviceTypeName)
                .orElseThrow(() -> new IllegalArgumentException("Service type not found: " + serviceTypeName));
    }

    private InfrastructureType findInfrastructureTypeByName(String infrastructureTypeName) {
        return infrastructureTypeRepository.findByInfrastructureName(infrastructureTypeName)
                .orElseThrow(() -> new IllegalArgumentException("Infrastructure type not found: " + infrastructureTypeName));
    }

    private Agent findAgent(String assignedAgentName, Department department) {
        if (assignedAgentName == null || assignedAgentName.isBlank()
                || "Unassigned".equalsIgnoreCase(assignedAgentName)) {
            return agentRepository.findByDepartment(department)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No agent found for department: " + department.getDepartmentName()
                    ));
        }

        return agentRepository.findByFullNameIgnoreCase(assignedAgentName)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + assignedAgentName));
    }

    private TicketResponseDto toResponseDto(Ticket ticket) {
        return new TicketResponseDto(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getCustomer() != null ? ticket.getCustomer().getId() : null,
                getCustomerName(ticket),
                ticket.getTopic() != null ? ticket.getTopic().getId() : null,
                getIssueTopicName(ticket),
                ticket.getCurrentDepartment() != null ? ticket.getCurrentDepartment().getId() : null,
                getDepartmentName(ticket),
                ticket.getRegion() != null ? ticket.getRegion().getId() : null,
                getCity(ticket),
                ticket.getAgent() != null ? ticket.getAgent().getId() : null,
                ticket.getAgent() != null ? ticket.getAgent().getFullName() : null,
                ticket.getServiceType() != null ? ticket.getServiceType().getId() : null,
                ticket.getServiceType() != null ? ticket.getServiceType().getServiceName() : null,
                ticket.getInfrastructureType() != null ? ticket.getInfrastructureType().getId() : null,
                getInfrastructureTypeName(ticket),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.isSlaBreached(),
                ticket.getResolutionTimeHours(),
                ticket.getCustomerSatisfactionScore(),
                ticket.getCreatedAt(),
                ticket.getResolvedAt(),
                ticket.getCreationSource()
        );
    }

    private String getCustomerName(Ticket ticket) {
        if (ticket.getCustomer() == null) {
            return null;
        }

        return ticket.getCustomer().getFirstName() + " " + ticket.getCustomer().getLastName();
    }

    private String getIssueTopicName(Ticket ticket) {
        if (ticket.getTopic() == null) {
            return null;
        }

        return ticket.getTopic().getTopicName();
    }

    private String getDepartmentName(Ticket ticket) {
        if (ticket.getCurrentDepartment() == null) {
            return null;
        }

        return ticket.getCurrentDepartment().getDepartmentName();
    }

    private String getCity(Ticket ticket) {
        if (ticket.getRegion() == null) {
            return null;
        }

        return ticket.getRegion().getCityName();
    }

    private String getInfrastructureTypeName(Ticket ticket) {
        if (ticket.getInfrastructureType() == null) {
            return null;
        }

        return ticket.getInfrastructureType().getInfrastructureName();
    }
}