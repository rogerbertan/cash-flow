package dev.rogerbertan.cash_flow.infra.dto;

import java.math.BigDecimal;

public record CategoriesSummaryResponse(
    String category,
    BigDecimal totalIncome,
    BigDecimal totalExpense
) {
}
