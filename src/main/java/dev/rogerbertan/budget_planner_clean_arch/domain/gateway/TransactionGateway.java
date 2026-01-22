package dev.rogerbertan.budget_planner_clean_arch.domain.gateway;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;

import java.util.List;

public interface TransactionGateway {

    List<Transaction> findAllTransactions();
    Transaction findTransactionById(Long id);
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(Transaction transaction);
    void deleteTransaction(Long id);
}
