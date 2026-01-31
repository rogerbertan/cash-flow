package dev.rogerbertan.cashflow.infra.gateway;

import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySummary;
import dev.rogerbertan.cashflow.infra.exception.InvalidTransactionException;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;
import dev.rogerbertan.cashflow.infra.mapper.TransactionEntityMapper;
import dev.rogerbertan.cashflow.infra.persistence.CategoryEntity;
import dev.rogerbertan.cashflow.infra.persistence.CategoryRepository;
import dev.rogerbertan.cashflow.infra.persistence.TransactionEntity;
import dev.rogerbertan.cashflow.infra.persistence.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionRepositoryGateway implements TransactionGateway {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionEntityMapper entityMapper;

    public TransactionRepositoryGateway(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            TransactionEntityMapper entityMapper) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public Page<Transaction> findAllTransactions(Pageable pageable) {

        Page<TransactionEntity> entities = transactionRepository.findAll(pageable);
        return entities.map(entityMapper::toDomain);
    }

    @Override
    public Transaction findTransactionById(Long id) {

        return transactionRepository.findById(id).map(entityMapper::toDomain).orElse(null);
    }

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {

        validateAmountPositive(transaction.amount());
        CategoryEntity category = validateTypeIsEqualCategoryType(transaction);

        TransactionEntity entity = entityMapper.toEntity(transaction);
        entity.setCategory(category);

        return entityMapper.toDomain(transactionRepository.save(entity));
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Transaction transaction) {

        validateAmountPositive(transaction.amount());
        validateTypeIsEqualCategoryType(transaction);

        TransactionEntity updatedEntity =
                transactionRepository.save(entityMapper.toEntity(transaction));

        return entityMapper.toDomain(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {

        transactionRepository.deleteById(id);
    }

    @Override
    public BigDecimal getTotalIncome() {
        return transactionRepository.sumAmountByType(Type.INCOME);
    }

    @Override
    public BigDecimal getTotalExpense() {
        return transactionRepository.sumAmountByType(Type.EXPENSE);
    }

    @Override
    public BigDecimal getMonthlyIncome(int month, int year) {
        return transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.INCOME);
    }

    @Override
    public BigDecimal getMonthlyExpense(int month, int year) {
        return transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.EXPENSE);
    }

    @Override
    public List<CategorySummary> getCategorySummaries(int month, int year) {
        return transactionRepository.findCategorySummariesByMonthAndYear(month, year);
    }

    @Override
    public List<Transaction> findTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByDateRange(startDate, endDate).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Map<DayOfWeek, BigDecimal> getExpensesByDayOfWeek(
            LocalDate startDate, LocalDate endDate) {
        List<TransactionEntity> transactions =
                transactionRepository.findByDateRange(startDate, endDate);

        Map<DayOfWeek, BigDecimal> expensesByDayOfWeek = new HashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            expensesByDayOfWeek.put(day, BigDecimal.ZERO);
        }

        for (TransactionEntity transaction : transactions) {
            if (transaction.getType() == Type.EXPENSE) {
                DayOfWeek dayOfWeek = transaction.getTransactionDate().getDayOfWeek();
                expensesByDayOfWeek.computeIfPresent(
                        dayOfWeek,
                        (k, currentAmount) -> currentAmount.add(transaction.getAmount()));
            }
        }

        return expensesByDayOfWeek;
    }

    @Override
    public Map<String, Long> getTransactionCountByCategory(LocalDate startDate, LocalDate endDate) {
        List<TransactionEntity> transactions =
                transactionRepository.findByDateRange(startDate, endDate);

        return transactions.stream()
                .collect(
                        Collectors.groupingBy(
                                t -> t.getCategory().getName(), Collectors.counting()));
    }

    @Override
    public Map<String, BigDecimal> getAverageAmountByCategory(
            LocalDate startDate, LocalDate endDate) {
        List<TransactionEntity> transactions =
                transactionRepository.findByDateRange(startDate, endDate);

        Map<String, List<BigDecimal>> amountsByCategory =
                transactions.stream()
                        .collect(
                                Collectors.groupingBy(
                                        t -> t.getCategory().getName(),
                                        Collectors.mapping(
                                                TransactionEntity::getAmount,
                                                Collectors.toList())));

        Map<String, BigDecimal> averages = new HashMap<>();
        for (Map.Entry<String, List<BigDecimal>> entry : amountsByCategory.entrySet()) {
            BigDecimal sum = entry.getValue().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal average =
                    sum.divide(
                            BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_UP);
            averages.put(entry.getKey(), average);
        }

        return averages;
    }

    private void validateAmountPositive(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be positive: " + amount);
        }
    }

    private CategoryEntity validateTypeIsEqualCategoryType(Transaction transaction) {

        CategoryEntity category =
                categoryRepository
                        .findById(transaction.category().id())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Category", "id: " + transaction.category().id()));

        if (!transaction.type().equals(category.getType())) {
            throw new InvalidTransactionException(
                    "Transaction type must be equal to category type");
        }

        return category;
    }
}
