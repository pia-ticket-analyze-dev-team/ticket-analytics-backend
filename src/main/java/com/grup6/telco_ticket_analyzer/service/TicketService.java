package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketRequestDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;
import com.grup6.telco_ticket_analyzer.exception.TicketNotFoundException;
import com.grup6.telco_ticket_analyzer.model.*;
import com.grup6.telco_ticket_analyzer.repository.TicketRepository;
import jakarta.persistence.EntityManager;
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
    private final EntityManager entityManager;

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
        Ticket ticket = new Ticket();
        applyRequestDto(ticket, requestDto);

        if (ticket.getCreatedAt() == null) {
            ticket.setCreatedAt(LocalDateTime.now());
        }

        return toResponseDto(ticketRepository.save(ticket));
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