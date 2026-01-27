package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.TransactionGateway;
import dev.rogerbertan.budget_planner_clean_arch.infra.exception.ResourceNotFoundException;

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
