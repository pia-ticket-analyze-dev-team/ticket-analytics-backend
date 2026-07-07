package com.grup6.telco_ticket_analyzer.exception;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID id) {
        super("Customer not found with id: " + id);
    }
}
