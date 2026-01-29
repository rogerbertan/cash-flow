package dev.rogerbertan.cash_flow.infra.dto;

import dev.rogerbertan.cash_flow.domain.enums.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Type type,
        BigDecimal amount,
        String description,
        Long category,
        LocalDate transactionDate,
        LocalDateTime createdAt
) {
}
