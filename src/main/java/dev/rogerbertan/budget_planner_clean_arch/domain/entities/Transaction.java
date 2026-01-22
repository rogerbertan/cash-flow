package dev.rogerbertan.budget_planner_clean_arch.domain.entities;

import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record Transaction(
        Long id,
        Type type,
        BigDecimal amount,
        String description,
        Category category,
        LocalDate transactionDate,
        LocalDateTime createdAt
) {
}
