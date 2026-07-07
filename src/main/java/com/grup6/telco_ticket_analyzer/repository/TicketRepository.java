package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.model.Ticket;
import com.grup6.telco_ticket_analyzer.repository.projection.ResolutionTimeProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.SatisfactionScoreProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

//analytics icin ekledigim importlar
import com.grup6.telco_ticket_analyzer.repository.projection.AgentPerformanceProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.CustomerChurnProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.ServiceTypeTrendProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.SlaTargetProjection;

import java.time.LocalDateTime;
import java.util.List;
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

    //count by status
    Long countByStatus(String status);  

    //issue topic based ticket count
    Long countByTopicIdAndStatus(UUID topicId, String status);

    //total ticket count as of a cutoff (for month-over-month KPI comparisons)
    long countByCreatedAtBefore(LocalDateTime cutoff);

    //open ticket count as of a cutoff (for month-over-month KPI comparisons)
    long countByStatusAndCreatedAtBefore(String status, LocalDateTime cutoff);

    //resolution times for averaging in the service layer
    List<ResolutionTimeProjection> findByResolutionTimeHoursIsNotNull();

    List<ResolutionTimeProjection> findByResolutionTimeHoursIsNotNullAndResolvedAtBefore(LocalDateTime cutoff);

    //satisfaction scores for averaging in the service layer
    List<SatisfactionScoreProjection> findByCustomerSatisfactionScoreIsNotNull();

    List<SatisfactionScoreProjection> findByCustomerSatisfactionScoreIsNotNullAndResolvedAtBefore(LocalDateTime cutoff);

    //analytics queries 
    List<CustomerChurnProjection> findByCustomerIsNotNullAndCreatedAtBetween(
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    List<AgentPerformanceProjection> findByAgentIsNotNullAndResolvedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<SlaTargetProjection> findByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<ServiceTypeTrendProjection> findByServiceTypeIsNotNullAndCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
);

}