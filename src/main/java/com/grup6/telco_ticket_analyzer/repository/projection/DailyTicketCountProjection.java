package com.grup6.telco_ticket_analyzer.repository.projection;

import java.time.LocalDate;

public interface DailyTicketCountProjection {

    LocalDate getDay();

    Long getTicketCount();
}
