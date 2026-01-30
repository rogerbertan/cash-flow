package dev.rogerbertan.cash_flow.domain.usecases.transaction;

import dev.rogerbertan.cash_flow.domain.entities.Transaction;
import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;
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
