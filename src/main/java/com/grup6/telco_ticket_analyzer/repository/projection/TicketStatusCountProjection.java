package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.repository.projection.TicketStatusCountProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface DashboardRepository extends Repository<Object, UUID> {

    @Query(value = """
            SELECT
                status AS status,
                COUNT(*) AS ticketCount
            FROM ticket
            GROUP BY status
            ORDER BY ticketCount DESC
            """, nativeQuery = true)
    List<TicketStatusCountProjection> getTicketStatusDistribution();

    @Query(value = """
            SELECT COUNT(*)
            FROM ticket
            """, nativeQuery = true)
    Long getTotalTicketCount();
}