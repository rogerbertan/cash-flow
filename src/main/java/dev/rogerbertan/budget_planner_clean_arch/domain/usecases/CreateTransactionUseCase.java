package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.TransactionGateway;

public class CreateTransactionUseCase {

    private final TransactionGateway transactionGateway;

    public CreateTransactionUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public Transaction execute(Transaction transaction) {

        return transactionGateway.createTransaction(transaction);
    }
}
