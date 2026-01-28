package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.TransactionGateway;
import dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects.MonthlySummary;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GetMonthlySummaryUseCase {

    private final TransactionGateway transactionGateway;

    public GetMonthlySummaryUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public MonthlySummary execute(int month, int year) {
        BigDecimal monthlyIncome = transactionGateway.getMonthlyIncome(month, year)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyExpense = transactionGateway.getMonthlyExpense(month, year)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyNetBalance = monthlyIncome.subtract(monthlyExpense)
                .setScale(2, RoundingMode.HALF_UP);

        return new MonthlySummary(monthlyIncome, monthlyExpense, monthlyNetBalance);
    }
}