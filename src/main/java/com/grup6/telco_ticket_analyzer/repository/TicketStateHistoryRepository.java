package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.model.TicketStateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketStateHistoryRepository extends JpaRepository<TicketStateHistory, UUID> {

    List<TicketStateHistory> findByTicketIdOrderByChangedAtAsc(UUID ticketId);

    List<TicketStateHistory> findByChangedByAgentId(UUID agentId);

    List<TicketStateHistory> findByNewDepartmentId(UUID departmentId);

    Optional<TicketStateHistory> findTopByTicketIdOrderByChangedAtDesc(UUID ticketId);
}