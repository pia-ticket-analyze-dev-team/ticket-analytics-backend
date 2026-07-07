package com.grup6.telco_ticket_analyzer.dto;

import java.util.List;

public record PagedResponseDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
