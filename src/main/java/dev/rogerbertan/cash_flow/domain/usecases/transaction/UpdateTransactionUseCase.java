package dev.rogerbertan.cash_flow.domain.usecases.transaction;

import dev.rogerbertan.cash_flow.domain.entities.Transaction;
import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cash_flow.infra.exception.ResourceNotFoundException;

import java.time.LocalDateTime;

public class UpdateTransactionUseCase {

    private final TransactionGateway transactionGateway;

    public UpdateTransactionUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public Transaction execute(Transaction transaction) {

        Transaction existingTransaction = transactionGateway.findTransactionById(transaction.id());
        if (existingTransaction == null) {
            throw new ResourceNotFoundException("Transaction", "id: " + transaction.id());
        }

        return transactionGateway.updateTransaction(
                new Transaction(
                        transaction.id(),
                        transaction.type(),
                        transaction.amount(),
                        transaction.description(),
                        transaction.category(),
                        transaction.transactionDate(),
                        LocalDateTime.now()
                )
        );
    }
}
