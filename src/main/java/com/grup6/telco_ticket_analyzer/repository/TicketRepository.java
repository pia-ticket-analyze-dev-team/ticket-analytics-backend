package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.model.Ticket;
import com.grup6.telco_ticket_analyzer.repository.projection.ResolutionTimeProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.SatisfactionScoreProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.AgentPerformanceProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.CustomerChurnProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.ServiceTypeTrendProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.SlaTargetProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID>, JpaSpecificationExecutor<Ticket> {

    Page<Ticket> findByCustomerId(UUID customerId, Pageable pageable);

    long countByCustomerId(UUID customerId);

    long countByCustomerIdAndStatusIn(UUID customerId, Collection<String> statuses);

    long countByCustomerIdAndSlaBreached(UUID customerId, boolean slaBreached);

    List<SatisfactionScoreProjection> findByCustomerIdAndCustomerSatisfactionScoreIsNotNull(UUID customerId);

    Page<Ticket> findByStatus(String status, Pageable pageable);

    Page<Ticket> findByPriority(String priority, Pageable pageable);

    Page<Ticket> findByRegionId(UUID regionId, Pageable pageable);

    Page<Ticket> findByTopicId(UUID topicId, Pageable pageable);

    Page<Ticket> findByCurrentDepartmentId(UUID currentDepartmentId, Pageable pageable);

    Page<Ticket> findByAgentId(UUID agentId, Pageable pageable);

    Page<Ticket> findBySlaBreached(Boolean slaBreached, Pageable pageable);

    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    Long countByRegionId(UUID regionId);

    Long countByTopicId(UUID topicId);

    Long countByCurrentDepartmentId(UUID currentDepartmentId);

    Long countByAgentId(UUID agentId);

    Long countByStatus(String status);

    Long countByTopicIdAndStatus(UUID topicId, String status);

    long countByCreatedAtBefore(LocalDateTime cutoff);

    long countByTicketNumberStartingWith(String prefix);

    long countByStatusAndCreatedAtBefore(String status, LocalDateTime cutoff);

    long countByRegion_GeographicalRegion(String geographicalRegion);

    long countBySlaBreached(boolean slaBreached);

    List<ResolutionTimeProjection> findByRegionIdAndResolutionTimeHoursIsNotNull(UUID regionId);

    List<ResolutionTimeProjection> findByRegion_GeographicalRegionAndResolutionTimeHoursIsNotNull(String geographicalRegion);

    List<ResolutionTimeProjection> findByResolutionTimeHoursIsNotNull();

    List<ResolutionTimeProjection> findByResolutionTimeHoursIsNotNullAndResolvedAtBefore(LocalDateTime cutoff);

    List<SatisfactionScoreProjection> findByCustomerSatisfactionScoreIsNotNull();

    List<SatisfactionScoreProjection> findByCustomerSatisfactionScoreIsNotNullAndResolvedAtBefore(LocalDateTime cutoff);

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