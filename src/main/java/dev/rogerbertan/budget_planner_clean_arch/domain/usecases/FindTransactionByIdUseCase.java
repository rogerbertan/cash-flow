package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.TransactionGateway;
import dev.rogerbertan.budget_planner_clean_arch.infra.exception.ResourceNotFoundException;

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
