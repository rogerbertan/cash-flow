package dev.rogerbertan.budget_planner_clean_arch.infra.dto;

import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;

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
