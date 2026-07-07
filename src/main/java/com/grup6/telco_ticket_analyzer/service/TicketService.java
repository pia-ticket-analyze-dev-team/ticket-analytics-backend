package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.NewCustomerDto;
import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketCreateDto;
import com.grup6.telco_ticket_analyzer.dto.TicketRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;
import com.grup6.telco_ticket_analyzer.exception.CustomerNotFoundException;
import com.grup6.telco_ticket_analyzer.exception.ReferenceDataNotFoundException;
import com.grup6.telco_ticket_analyzer.exception.TicketNotFoundException;
import com.grup6.telco_ticket_analyzer.model.*;
import com.grup6.telco_ticket_analyzer.repository.AgentRepository;
import com.grup6.telco_ticket_analyzer.repository.CustomerRepository;
import com.grup6.telco_ticket_analyzer.repository.DepartmentRepository;
import com.grup6.telco_ticket_analyzer.repository.InfrastructureTypeRepository;
import com.grup6.telco_ticket_analyzer.repository.IssueTopicRepository;
import com.grup6.telco_ticket_analyzer.repository.RegionRepository;
import com.grup6.telco_ticket_analyzer.repository.ServiceTypeRepository;
import com.grup6.telco_ticket_analyzer.repository.TicketRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService implements TicketServiceInterface {

    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_TICKET_NUMBER_RETRIES = 2;
    private static final String WEB_CREATION_SOURCE = "CALL_CENTER";

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final DepartmentRepository departmentRepository;
    private final IssueTopicRepository issueTopicRepository;
    private final RegionRepository regionRepository;
    private final AgentRepository agentRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final InfrastructureTypeRepository infrastructureTypeRepository;
    private final EntityManager entityManager;

    @Override
    public PagedResponseDto<TicketResponseDto> getAllTickets(
            int page,
            int size,
            String status,
            String priority,
            UUID topicId,
            UUID departmentId,
            UUID regionId,
            Boolean slaBreached,
            UUID agentId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                clampSize(size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Ticket> ticketPage = ticketRepository.findAll(
                buildTicketSpecification(
                        status,
                        priority,
                        topicId,
                        departmentId,
                        regionId,
                        slaBreached,
                        agentId,
                        startDate,
                        endDate
                ),
                pageable
        );

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
    public PagedResponseDto<TicketResponseDto> getTicketsByAgentId(UUID agentId, int page, int size) {
        agentRepository.findById(agentId)
                .orElseThrow(() -> new ReferenceDataNotFoundException("Agent", agentId));

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                clampSize(size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Ticket> ticketPage = ticketRepository.findByAgentId(agentId, pageable);

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
    public PagedResponseDto<TicketResponseDto> getTicketsByCustomerId(UUID customerId, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                clampSize(size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Ticket> ticketPage = ticketRepository.findByCustomerId(customerId, pageable);

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

    private int clampSize(int size) {
        if (size < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    @Override
    @Transactional
    public TicketResponseDto createTicket(TicketCreateDto requestDto) {
        Customer customer = requestDto.customerId() != null
                ? customerRepository.findById(requestDto.customerId())
                        .orElseThrow(() -> new CustomerNotFoundException(requestDto.customerId()))
                : customerRepository.save(toNewCustomer(requestDto.newCustomer()));

        IssueTopic topic = issueTopicRepository.findById(requestDto.topicId())
                .orElseThrow(() -> new ReferenceDataNotFoundException("Issue topic", requestDto.topicId()));
        Department department = departmentRepository.findById(requestDto.currentDepartmentId())
                .orElseThrow(() -> new ReferenceDataNotFoundException("Department", requestDto.currentDepartmentId()));
        Agent agent = agentRepository.findById(requestDto.agentId())
                .orElseThrow(() -> new ReferenceDataNotFoundException("Agent", requestDto.agentId()));
        Region region = regionRepository.findById(requestDto.regionId())
                .orElseThrow(() -> new ReferenceDataNotFoundException("Region", requestDto.regionId()));
        ServiceType serviceType = serviceTypeRepository.findById(requestDto.serviceTypeId())
                .orElseThrow(() -> new ReferenceDataNotFoundException("Service type", requestDto.serviceTypeId()));
        InfrastructureType infrastructureType = infrastructureTypeRepository.findById(requestDto.infrastructureTypeId())
                .orElseThrow(() -> new ReferenceDataNotFoundException("Infrastructure type", requestDto.infrastructureTypeId()));

        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setTopic(topic);
        ticket.setCurrentDepartment(department);
        ticket.setAgent(agent);
        ticket.setRegion(region);
        ticket.setServiceType(serviceType);
        ticket.setInfrastructureType(infrastructureType);
        ticket.setDescription(requestDto.description());
        ticket.setStatus(requestDto.status());
        ticket.setPriority(requestDto.priority());
        ticket.setSlaBreached(requestDto.slaBreached());
        ticket.setResolutionTimeHours(requestDto.resolutionTimeHours());
        ticket.setCustomerSatisfactionScore(requestDto.customerSatisfactionScore());
        ticket.setCreatedAt(requestDto.createdAt() != null ? requestDto.createdAt() : LocalDateTime.now());
        ticket.setResolvedAt(requestDto.resolvedAt());
        ticket.setCreationSource(WEB_CREATION_SOURCE);

        return toResponseDto(saveWithGeneratedTicketNumber(ticket));
    }

    private Customer toNewCustomer(NewCustomerDto newCustomerDto) {
        Customer customer = new Customer();
        customer.setFirstName(newCustomerDto.firstName());
        customer.setLastName(newCustomerDto.lastName());
        customer.setEmail(newCustomerDto.email());
        customer.setAddress(newCustomerDto.address());
        customer.setBirthdate(newCustomerDto.birthdate());
        customer.setPhone(newCustomerDto.phone());
        customer.setSegment(newCustomerDto.segment());
        customer.setCreatedAt(LocalDate.now());
        return customer;
    }

    private Ticket saveWithGeneratedTicketNumber(Ticket ticket) {
        DataIntegrityViolationException lastFailure = null;
        for (int attempt = 0; attempt <= MAX_TICKET_NUMBER_RETRIES; attempt++) {
            ticket.setTicketNumber(generateTicketNumber(ticket.getCreatedAt(), attempt));
            try {
                return ticketRepository.saveAndFlush(ticket);
            } catch (DataIntegrityViolationException ex) {
                lastFailure = ex;
            }
        }
        throw lastFailure;
    }

    private String generateTicketNumber(LocalDateTime createdAt, int attemptOffset) {
        String prefix = "TCK-" + createdAt.format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        long sequence = ticketRepository.countByTicketNumberStartingWith(prefix) + 1 + attemptOffset;
        return prefix + String.format("%05d", sequence);
    }

    @Override
    public TicketResponseDto updateTicket(UUID id, TicketRequestDto requestDto) {
        Ticket ticket = findTicketOrThrow(id);
        applyRequestDto(ticket, requestDto);

        return toResponseDto(ticketRepository.save(ticket));
    }

    @Override
    public void deleteTicket(UUID id) {
        ticketRepository.delete(findTicketOrThrow(id));
    }

    private Specification<Ticket> buildTicketSpecification(
            String status,
            String priority,
            UUID topicId,
            UUID departmentId,
            UUID regionId,
            Boolean slaBreached,
            UUID agentId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (priority != null && !priority.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }

            if (topicId != null) {
                predicates.add(criteriaBuilder.equal(root.get("topic").get("id"), topicId));
            }

            if (departmentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("currentDepartment").get("id"), departmentId));
            }

            if (regionId != null) {
                predicates.add(criteriaBuilder.equal(root.get("region").get("id"), regionId));
            }

            if (slaBreached != null) {
                predicates.add(criteriaBuilder.equal(root.get("slaBreached"), slaBreached));
            }

            if (agentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("agent").get("id"), agentId));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Ticket findTicketOrThrow(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    private void applyRequestDto(Ticket ticket, TicketRequestDto requestDto) {
        ticket.setTicketNumber(requestDto.ticketNumber());
        ticket.setCustomer(getReference(Customer.class, requestDto.customerId()));
        ticket.setTopic(getReference(IssueTopic.class, requestDto.topicId()));
        ticket.setCurrentDepartment(getReference(Department.class, requestDto.currentDepartmentId()));
        ticket.setAgent(getReference(Agent.class, requestDto.agentId()));
        ticket.setRegion(getReference(Region.class, requestDto.regionId()));
        ticket.setServiceType(getReference(ServiceType.class, requestDto.serviceTypeId()));
        ticket.setInfrastructureType(getReference(InfrastructureType.class, requestDto.infrastructureTypeId()));
        ticket.setDescription(requestDto.description());
        ticket.setStatus(requestDto.status());
        ticket.setPriority(requestDto.priority());
        ticket.setSlaBreached(requestDto.slaBreached());
        ticket.setResolutionTimeHours(requestDto.resolutionTimeHours());
        ticket.setCustomerSatisfactionScore(requestDto.customerSatisfactionScore());
        ticket.setCreatedAt(requestDto.createdAt());
        ticket.setResolvedAt(requestDto.resolvedAt());
        ticket.setCreationSource(requestDto.creationSource());
    }

    private <T> T getReference(Class<T> entityClass, UUID id) {
        if (id == null) {
            return null;
        }

        return entityManager.getReference(entityClass, id);
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