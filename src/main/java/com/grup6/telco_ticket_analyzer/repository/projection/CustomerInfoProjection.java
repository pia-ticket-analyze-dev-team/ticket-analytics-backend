package com.grup6.telco_ticket_analyzer.repository.projection;

import java.util.UUID;

public interface CustomerInfoProjection {

    UUID getId();

    String getFirstName();

    String getLastName();

    String getSegment();
}