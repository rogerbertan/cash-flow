package dev.rogerbertan.cashflow.domain.usecases.transaction;

import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;

public class FindTransactionByIdUseCase {

    private final TransactionGateway transactionGateway;

    public FindTransactionByIdUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public Transaction execute(Long id) {
        Transaction transaction = transactionGateway.findTransactionById(id);

        if (transaction == null) {
            throw new ResourceNotFoundException("Transaction", "id: " + id);
        }

        return transaction;
    }
}
