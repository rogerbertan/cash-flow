package dev.rogerbertan.cashflow.application.usecases.transaction;

import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.application.gateway.TransactionGateway;

public class CreateTransactionUseCase {

    private final TransactionGateway transactionGateway;

    public CreateTransactionUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public Transaction execute(Transaction transaction) {

        return transactionGateway.createTransaction(transaction);
    }
}
