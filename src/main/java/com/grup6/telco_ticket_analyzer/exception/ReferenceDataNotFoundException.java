package com.grup6.telco_ticket_analyzer.exception;

import java.util.UUID;

public class ReferenceDataNotFoundException extends RuntimeException {

    public ReferenceDataNotFoundException(String entityType, UUID id) {
        super(entityType + " not found with id: " + id);
    }
}
