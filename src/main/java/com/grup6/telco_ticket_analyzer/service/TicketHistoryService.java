package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.TicketDepartmentHistoryDto;
import com.grup6.telco_ticket_analyzer.model.TicketStateHistory;
import com.grup6.telco_ticket_analyzer.repository.TicketStateHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketHistoryService {

    private final TicketStateHistoryRepository ticketStateHistoryRepository;

    public List<TicketDepartmentHistoryDto> getDepartmentHistoryByTicketId(UUID ticketId) {
        return ticketStateHistoryRepository.findByTicketIdOrderByChangedAtAsc(ticketId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private TicketDepartmentHistoryDto toDto(TicketStateHistory history) {
        return new TicketDepartmentHistoryDto(
                history.getId(),
                history.getTicket().getId(),
                history.getPreviousDepartment() != null ? history.getPreviousDepartment().getId() : null,
                history.getPreviousDepartment() != null ? history.getPreviousDepartment().getDepartmentName() : null,
                history.getNewDepartment() != null ? history.getNewDepartment().getId() : null,
                history.getNewDepartment() != null ? history.getNewDepartment().getDepartmentName() : null,
                history.getChangedByAgent() != null ? history.getChangedByAgent().getId() : null,
                history.getChangedByAgent() != null ? history.getChangedByAgent().getFullName() : null,
                history.getPreviousStatus(),
                history.getNewStatus(),
                history.getActionType(),
                history.getChangedAt(),
                history.getDurationMinutes()
        );
    }
}