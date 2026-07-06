package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.repository.projection.DailyTicketCountProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.NamedCountProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.TicketStatusCountProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface DashboardRepository extends Repository<Object, UUID> {

    @Query(value = """
            SELECT COUNT(*)
            FROM ticket
            """, nativeQuery = true)
    Long getTotalTicketCount();

    @Query(value = """
            SELECT COUNT(*)
            FROM customer
            """, nativeQuery = true)
    Long getTotalCustomerCount();

    @Query(value = """
            SELECT COUNT(*)
            FROM ticket
            WHERE status = 'OPEN'
            """, nativeQuery = true)
    Long getOpenTicketCount();

    @Query(value = """
            SELECT ROUND(100.0 * COUNT(*) FILTER (WHERE is_sla_breached) / NULLIF(COUNT(*), 0), 2)
            FROM ticket
            """, nativeQuery = true)
    Double getSlaBreachRatePct();

    @Query(value = """
            SELECT ROUND(AVG(resolution_time_hours), 2)
            FROM ticket
            WHERE resolution_time_hours IS NOT NULL
            """, nativeQuery = true)
    Double getAverageResolutionTimeHours();

    @Query(value = """
            SELECT ROUND(AVG(customer_satisfaction_score), 2)
            FROM ticket
            WHERE customer_satisfaction_score IS NOT NULL
            """, nativeQuery = true)
    Double getAverageCustomerSatisfaction();

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
            SELECT
                CAST(t.created_at AS DATE) AS day,
                COUNT(*) AS ticketCount
            FROM ticket t, (SELECT MAX(created_at)::date AS max_day FROM ticket) b
            WHERE t.created_at >= b.max_day - INTERVAL '29 days'
            GROUP BY day
            ORDER BY day
            """, nativeQuery = true)
    List<DailyTicketCountProjection> getTicketVolumeLast30Days();

    @Query(value = """
            SELECT
                r.city_name AS name,
                COUNT(*) AS ticketCount
            FROM ticket t
            JOIN region r ON r.id = t.region_id
            GROUP BY r.city_name
            ORDER BY ticketCount DESC
            LIMIT 5
            """, nativeQuery = true)
    List<NamedCountProjection> getTopRegionsByTicketCount();

    @Query(value = """
            SELECT
                it.topic_name AS name,
                COUNT(*) AS ticketCount
            FROM ticket t
            JOIN issue_topic it ON it.id = t.topic_id
            GROUP BY it.topic_name
            ORDER BY ticketCount DESC
            LIMIT 5
            """, nativeQuery = true)
    List<NamedCountProjection> getTopIssueTopics();

    @Query(value = """
            SELECT
                d.department_name AS name,
                COUNT(*) AS ticketCount
            FROM ticket t
            JOIN department d ON d.id = t.current_department_id
            GROUP BY d.department_name
            ORDER BY ticketCount DESC
            """, nativeQuery = true)
    List<NamedCountProjection> getDepartmentWorkload();
}
