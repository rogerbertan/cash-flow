package dev.rogerbertan.budget_planner_clean_arch.infra.dto;

import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;

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
