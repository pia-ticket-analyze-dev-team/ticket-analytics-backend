package com.grup6.telco_ticket_analyzer.repository.projection;

import java.util.UUID;

public interface ServiceTypeInfoProjection {

    UUID getId();

    String getServiceName();
}