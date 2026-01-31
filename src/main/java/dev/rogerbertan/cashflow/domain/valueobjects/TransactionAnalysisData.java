package dev.rogerbertan.cashflow.domain.valueobjects;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record TransactionAnalysisData(
        LocalDate startDate,
        LocalDate endDate,
        String period,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal previousPeriodIncome,
        BigDecimal previousPeriodExpense,
        List<CategorySummary> categorySummaries,
        List<CategorySummary> previousCategorySummaries,
        Map<DayOfWeek, BigDecimal> expensesByDayOfWeek,
        Map<String, Long> transactionCountByCategory,
        Map<String, BigDecimal> averageAmountByCategory) {}
