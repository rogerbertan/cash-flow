package dev.rogerbertan.cashflow.domain.usecases.summary;

import dev.rogerbertan.cashflow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySummary;
import java.math.RoundingMode;
import java.util.List;

public class GetCategoriesSummaryUseCase {

    private final TransactionGateway transactionGateway;

    public GetCategoriesSummaryUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public List<CategorySummary> execute(int month, int year) {
        List<CategorySummary> summaries = transactionGateway.getCategorySummaries(month, year);

        return summaries.stream()
                .map(
                        summary ->
                                new CategorySummary(
                                        summary.categoryName(),
                                        summary.totalIncome().setScale(2, RoundingMode.HALF_UP),
                                        summary.totalExpense().setScale(2, RoundingMode.HALF_UP)))
                .toList();
    }
}
