package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.TransactionGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class FindAllTransactionUseCase {

    private final TransactionGateway transactionGateway;

    public FindAllTransactionUseCase(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public Page<Transaction> execute(Pageable pageable) {

        return transactionGateway.findAllTransactions(pageable);
    }
}
