package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.TransactionGateway;

import java.time.LocalDateTime;

public class UpdateTransactionUseCase {

    private final TransactionGateway transactionGateway;

    public UpdateTransactionUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public Transaction execute(Transaction transaction) {

        Transaction existingTransaction = transactionGateway.findTransactionById(transaction.id());
        if (existingTransaction == null) {
            throw new IllegalArgumentException("Transaction not found with id: " + transaction.id());
        }

        return new Transaction(
                transaction.id(),
                transaction.type(),
                transaction.amount(),
                transaction.description(),
                transaction.category(),
                transaction.transactionDate(),
                LocalDateTime.now()
        );
    }
}
