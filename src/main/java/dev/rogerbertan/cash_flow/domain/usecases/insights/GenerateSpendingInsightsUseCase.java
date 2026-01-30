package dev.rogerbertan.cash_flow.domain.usecases.insights;

import dev.rogerbertan.cash_flow.domain.entities.Transaction;
import dev.rogerbertan.cash_flow.domain.enums.Type;
import dev.rogerbertan.cash_flow.domain.gateway.AIInsightsGateway;
import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cash_flow.domain.valueobjects.CategorySummary;
import dev.rogerbertan.cash_flow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cash_flow.domain.valueobjects.TransactionAnalysisData;
import dev.rogerbertan.cash_flow.infra.util.DateRange;
import dev.rogerbertan.cash_flow.infra.util.PeriodCalculator;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public class GenerateSpendingInsightsUseCase {

    private final AIInsightsGateway aiInsightsGateway;
    private final TransactionGateway transactionGateway;

    public GenerateSpendingInsightsUseCase(
            AIInsightsGateway aiInsightsGateway,
            TransactionGateway transactionGateway
    ) {
        this.aiInsightsGateway = aiInsightsGateway;
        this.transactionGateway = transactionGateway;
    }

    public SpendingInsights execute(String period) {
        TransactionAnalysisData analysisData = buildAnalysisData(period);

        List<Transaction> currentTransactions = transactionGateway.findTransactionsByDateRange(
                analysisData.startDate(),
                analysisData.endDate()
        );

        if (currentTransactions.isEmpty()) {
            return new SpendingInsights(
                    List.of("No transactions found for this period. Start tracking your expenses!"),
                    period,
                    "No data available",
                    "no_transactions"
            );
        }

        if (currentTransactions.size() < 3) {
            return new SpendingInsights(
                    List.of("Insufficient data for analysis. Add more transactions to get insights."),
                    period,
                    "Not enough data",
                    "insufficient_data"
            );
        }

        return aiInsightsGateway.generateInsights(analysisData);
    }

    private TransactionAnalysisData buildAnalysisData(String period) {
        DateRange currentPeriod = PeriodCalculator.getCurrentPeriod(period);
        DateRange previousPeriod = PeriodCalculator.getPreviousPeriod(period);

        BigDecimal totalIncome = calculateTotalByType(currentPeriod, Type.INCOME);
        BigDecimal totalExpense = calculateTotalByType(currentPeriod, Type.EXPENSE);

        BigDecimal previousIncome = calculateTotalByType(previousPeriod, Type.INCOME);
        BigDecimal previousExpense = calculateTotalByType(previousPeriod, Type.EXPENSE);

        List<CategorySummary> categorySummaries = getCategorySummariesForDateRange(currentPeriod);
        List<CategorySummary> previousCategorySummaries = getCategorySummariesForDateRange(previousPeriod);

        Map<DayOfWeek, BigDecimal> expensesByDayOfWeek = transactionGateway.getExpensesByDayOfWeek(
                currentPeriod.start(),
                currentPeriod.end()
        );

        Map<String, Long> transactionCountByCategory = transactionGateway.getTransactionCountByCategory(
                currentPeriod.start(),
                currentPeriod.end()
        );

        Map<String, BigDecimal> averageAmountByCategory = transactionGateway.getAverageAmountByCategory(
                currentPeriod.start(),
                currentPeriod.end()
        );

        return new TransactionAnalysisData(
                currentPeriod.start(),
                currentPeriod.end(),
                period,
                totalIncome,
                totalExpense,
                previousIncome,
                previousExpense,
                categorySummaries,
                previousCategorySummaries,
                expensesByDayOfWeek,
                transactionCountByCategory,
                averageAmountByCategory
        );
    }

    private BigDecimal calculateTotalByType(DateRange dateRange, Type type) {
        List<Transaction> transactions = transactionGateway.findTransactionsByDateRange(
                dateRange.start(),
                dateRange.end()
        );

        return transactions.stream()
                .filter(t -> t.type() == type)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<CategorySummary> getCategorySummariesForDateRange(DateRange dateRange) {
        List<Transaction> transactions = transactionGateway.findTransactionsByDateRange(
                dateRange.start(),
                dateRange.end()
        );

        Map<String, BigDecimal> incomeByCategory = new java.util.HashMap<>();
        Map<String, BigDecimal> expenseByCategory = new java.util.HashMap<>();

        for (Transaction transaction : transactions) {
            String categoryName = transaction.category().name();

            if (transaction.type() == Type.INCOME) {
                incomeByCategory.merge(categoryName, transaction.amount(), BigDecimal::add);
            } else {
                expenseByCategory.merge(categoryName, transaction.amount(), BigDecimal::add);
            }
        }

        java.util.Set<String> allCategories = new java.util.HashSet<>();
        allCategories.addAll(incomeByCategory.keySet());
        allCategories.addAll(expenseByCategory.keySet());

        return allCategories.stream()
                .map(categoryName -> new CategorySummary(
                        categoryName,
                        incomeByCategory.getOrDefault(categoryName, BigDecimal.ZERO),
                        expenseByCategory.getOrDefault(categoryName, BigDecimal.ZERO)
                ))
                .collect(java.util.stream.Collectors.toList());
    }
}