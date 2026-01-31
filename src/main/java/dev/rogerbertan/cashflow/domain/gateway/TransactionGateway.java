package dev.rogerbertan.cashflow.domain.gateway;

import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySummary;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    List<Transaction> findTransactionsByDateRange(LocalDate startDate, LocalDate endDate);

    Map<DayOfWeek, BigDecimal> getExpensesByDayOfWeek(LocalDate startDate, LocalDate endDate);

    Map<String, Long> getTransactionCountByCategory(LocalDate startDate, LocalDate endDate);

    Map<String, BigDecimal> getAverageAmountByCategory(LocalDate startDate, LocalDate endDate);
}
