package dev.rogerbertan.cashflow.infra.dto;

import java.math.BigDecimal;

public record CategoriesSummaryResponse(
        String category, BigDecimal totalIncome, BigDecimal totalExpense) {}
