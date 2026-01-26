package dev.rogerbertan.budget_planner_clean_arch.domain.gateway;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionGateway {

    Page<Transaction> findAllTransactions(Pageable pageable);
    Transaction findTransactionById(Long id);
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(Transaction transaction);
    void deleteTransaction(Long id);
}
