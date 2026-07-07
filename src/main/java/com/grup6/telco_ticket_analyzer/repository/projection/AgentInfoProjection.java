package com.grup6.telco_ticket_analyzer.repository.projection;

import java.util.UUID;

public interface AgentInfoProjection {

    UUID getId();

    String getFullName();
}