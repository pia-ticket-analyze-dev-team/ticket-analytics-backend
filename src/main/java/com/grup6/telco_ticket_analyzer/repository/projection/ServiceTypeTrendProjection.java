package com.grup6.telco_ticket_analyzer.repository.projection;

import java.time.LocalDateTime;

public interface ServiceTypeTrendProjection {

    ServiceTypeInfoProjection getServiceType();

    LocalDateTime getCreatedAt();
}