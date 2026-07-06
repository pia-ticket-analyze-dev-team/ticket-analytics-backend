package com.grup6.telco_ticket_analyzer.dto;

import java.time.LocalDate;

public record DailyTicketVolumeDto(
        LocalDate date,
        long ticketCount
) {
}
