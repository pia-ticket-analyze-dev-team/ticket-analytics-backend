package com.grup6.telco_ticket_analyzer.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CustomerChurnProjection {

    CustomerInfoProjection getCustomer();

    boolean isSlaBreached();

    BigDecimal getResolutionTimeHours();

    Integer getCustomerSatisfactionScore();

    LocalDateTime getCreatedAt();
}