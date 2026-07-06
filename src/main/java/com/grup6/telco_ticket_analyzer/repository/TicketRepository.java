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

    //ticket count on last 30 days and total count
    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);   
    
   //region based ticket count
    Long countByRegionId(UUID regionId);

    //topic based ticket count
    Long countByTopicId(UUID topicId);

    //department based ticket count
    Long countByCurrentDepartmentId(UUID currentDepartmentId);

    //agent based ticket count
    Long countByAgentId(UUID agentId);   
    
    //count all tickets  
    Long countAll();

    //count by status
    Long countByStatus(String status);  

    //issue topic based ticket count
    Long countByTopicIdAndStatus(UUID topicId, String status);


}