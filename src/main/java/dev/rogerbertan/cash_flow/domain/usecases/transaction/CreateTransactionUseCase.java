package dev.rogerbertan.cash_flow.domain.usecases.transaction;

import dev.rogerbertan.cash_flow.domain.entities.Transaction;
import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;

public class CreateTransactionUseCase {

    private final TransactionGateway transactionGateway;

    public CreateTransactionUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public Transaction execute(Transaction transaction) {

        return transactionGateway.createTransaction(transaction);
    }
}
