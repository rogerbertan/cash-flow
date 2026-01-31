package dev.rogerbertan.cashflow.domain.usecases.transaction;

import dev.rogerbertan.cashflow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;

public class DeleteTransactionUseCase {

    private final TransactionGateway transactionGateway;

    public DeleteTransactionUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public void execute(Long id) {

        if (transactionGateway.findTransactionById(id) == null) {
            throw new ResourceNotFoundException("Transaction", "id: " + id);
        }

        transactionGateway.deleteTransaction(id);
    }
}
