package com.grup6.telco_ticket_analyzer.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Invalid email or password.");
    }
}
