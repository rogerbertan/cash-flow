package dev.rogerbertan.cashflow.application.usecases.transaction;

import dev.rogerbertan.cashflow.application.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.domain.entities.Transaction;

public class CreateTransactionUseCase {

    private final TransactionGateway transactionGateway;

    public CreateTransactionUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public Transaction execute(Transaction transaction) {

        return transactionGateway.createTransaction(transaction);
    }
}
