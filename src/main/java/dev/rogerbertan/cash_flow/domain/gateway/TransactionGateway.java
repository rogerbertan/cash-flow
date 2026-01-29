package dev.rogerbertan.cash_flow.domain.gateway;

import dev.rogerbertan.cash_flow.domain.entities.Transaction;
import dev.rogerbertan.cash_flow.domain.valueobjects.CategorySummary;
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
