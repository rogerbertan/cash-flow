package dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects;

import java.math.BigDecimal;

public record CategorySummary(
        String categoryName,
        BigDecimal totalIncome,
        BigDecimal totalExpense
) {
}