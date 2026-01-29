package dev.rogerbertan.cash_flow.domain.usecases;

import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cash_flow.domain.valueobjects.Balance;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GetBalanceUseCase {

    private final TransactionGateway transactionGateway;

    public GetBalanceUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public Balance execute() {
        BigDecimal totalIncome = transactionGateway.getTotalIncome();
        BigDecimal totalExpense = transactionGateway.getTotalExpense();
        BigDecimal netBalance = totalIncome.subtract(totalExpense)
                .setScale(2, RoundingMode.HALF_UP);

        return new Balance(netBalance);
    }
}