package dev.rogerbertan.budget_planner_clean_arch.infra.dto;

import java.math.BigDecimal;

public record MonthlySummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
) {
}
