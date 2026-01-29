package dev.rogerbertan.cash_flow.domain.usecases;

import dev.rogerbertan.cash_flow.domain.entities.Transaction;
import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cash_flow.infra.exception.ResourceNotFoundException;

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
