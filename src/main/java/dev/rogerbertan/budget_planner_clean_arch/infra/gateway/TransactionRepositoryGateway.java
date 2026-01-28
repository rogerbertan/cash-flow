package dev.rogerbertan.budget_planner_clean_arch.infra.gateway;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.TransactionGateway;
import dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects.CategorySummary;
import dev.rogerbertan.budget_planner_clean_arch.infra.exception.InvalidTransactionException;
import dev.rogerbertan.budget_planner_clean_arch.infra.exception.ResourceNotFoundException;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.TransactionEntityMapper;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.CategoryEntity;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.CategoryRepository;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.TransactionEntity;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class TransactionRepositoryGateway implements TransactionGateway {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionEntityMapper entityMapper;

    public TransactionRepositoryGateway(TransactionRepository transactionRepository, CategoryRepository categoryRepository, TransactionEntityMapper entityMapper) {
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

        return transactionRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElse(null);
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

        TransactionEntity updatedEntity = transactionRepository.save(entityMapper.toEntity(transaction));

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

    private void validateAmountPositive(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be positive: " + amount);
        }
    }

    private CategoryEntity validateTypeIsEqualCategoryType(Transaction transaction) {

        CategoryEntity category = categoryRepository.findById(transaction.category().id())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id: " + transaction.category().id()));

        if (!transaction.type().equals(category.getType())) {
            throw new InvalidTransactionException("Transaction type must be equal to category type");
        }

        return category;
    }
}
