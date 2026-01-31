package dev.rogerbertan.cashflow.domain.usecases.summary;

import dev.rogerbertan.cashflow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.domain.valueobjects.Balance;
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
        BigDecimal netBalance =
                totalIncome.subtract(totalExpense).setScale(2, RoundingMode.HALF_UP);

        return new Balance(netBalance);
    }
}
