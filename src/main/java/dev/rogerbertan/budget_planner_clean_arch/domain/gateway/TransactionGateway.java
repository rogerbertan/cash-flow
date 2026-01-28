package dev.rogerbertan.budget_planner_clean_arch.domain.gateway;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects.CategorySummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionGateway {

    Page<Transaction> findAllTransactions(Pageable pageable);
    Transaction findTransactionById(Long id);
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(Transaction transaction);
    void deleteTransaction(Long id);

    BigDecimal getTotalIncome();
    BigDecimal getTotalExpense();
    BigDecimal getMonthlyIncome(int month, int year);
    BigDecimal getMonthlyExpense(int month, int year);
    List<CategorySummary> getCategorySummaries(int month, int year);
}
