package com.grup6.telco_ticket_analyzer.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AgentPerformanceProjection {

    AgentInfoProjection getAgent();

    boolean isSlaBreached();

    BigDecimal getResolutionTimeHours();

    Integer getCustomerSatisfactionScore();

    LocalDateTime getResolvedAt();
}