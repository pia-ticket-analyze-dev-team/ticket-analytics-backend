package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    Page<Ticket> findByCustomerId(UUID customerId, Pageable pageable);

    Page<Ticket> findByStatus(String status, Pageable pageable);

    Page<Ticket> findByPriority(String priority, Pageable pageable);

    Page<Ticket> findByRegionId(UUID regionId, Pageable pageable);

    Page<Ticket> findByTopicId(UUID topicId, Pageable pageable);

    Page<Ticket> findByCurrentDepartmentId(UUID currentDepartmentId, Pageable pageable);

    Page<Ticket> findByAgentId(UUID agentId, Pageable pageable);

    Page<Ticket> findBySlaBreached(Boolean slaBreached, Pageable pageable);

    Page<Ticket> findByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );
}