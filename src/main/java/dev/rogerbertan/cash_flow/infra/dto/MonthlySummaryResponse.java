package dev.rogerbertan.cash_flow.infra.dto;

import java.math.BigDecimal;

public record MonthlySummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
) {
}
