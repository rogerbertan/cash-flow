package dev.rogerbertan.cash_flow.infra.dto;

import dev.rogerbertan.cash_flow.domain.enums.Type;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionCreateRequest(
        Type type,
        BigDecimal amount,
        String description,
        Long categoryId,
        LocalDate transactionDate
) {
}
