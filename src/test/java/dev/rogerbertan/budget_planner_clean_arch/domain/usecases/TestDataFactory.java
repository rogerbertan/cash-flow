package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.CategoryEntity;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestDataFactory {

    public static Category createCategory(Long id, String name, Type type) {
        return new Category(id, name, type, LocalDateTime.now());
    }

    public static Category createIncomeCategory() {
        return createCategory(1L, "Salary", Type.INCOME);
    }

    public static Category createExpenseCategory() {
        return createCategory(2L, "Food", Type.EXPENSE);
    }

    public static Transaction createTransaction(Long id, Category category, BigDecimal amount) {
        return new Transaction(
                id,
                category.type(),
                amount,
                "Test transaction",
                category,
                LocalDate.now(),
                LocalDateTime.now()
        );
    }

    public static Transaction createIncomeTransaction() {
        return createTransaction(1L, createIncomeCategory(), new BigDecimal("1000.00"));
    }

    public static Transaction createExpenseTransaction() {
        return createTransaction(2L, createExpenseCategory(), new BigDecimal("500.00"));
    }

    // JPA Entity creation methods for gateway tests

    public static CategoryEntity createCategoryEntity(Long id, String name, Type type, LocalDateTime createdAt) {
        return new CategoryEntity(id, name, type, createdAt);
    }

    public static CategoryEntity createIncomeCategoryEntity() {
        return createCategoryEntity(1L, "Salary", Type.INCOME, LocalDateTime.now().minusDays(1));
    }

    public static CategoryEntity createExpenseCategoryEntity() {
        return createCategoryEntity(2L, "Food", Type.EXPENSE, LocalDateTime.now().minusDays(1));
    }

    public static CategoryEntity createCategoryEntityWithType(Type type) {
        return createCategoryEntity(
                type == Type.INCOME ? 1L : 2L,
                type == Type.INCOME ? "Salary" : "Food",
                type,
                LocalDateTime.now().minusDays(1)
        );
    }

    public static TransactionEntity createTransactionEntity(
            Long id,
            Type type,
            BigDecimal amount,
            String description,
            CategoryEntity category,
            LocalDate transactionDate,
            LocalDateTime createdAt
    ) {
        TransactionEntity entity = new TransactionEntity(type, amount, description, category, transactionDate);
        entity.setId(id);
        entity.setCreatedAt(createdAt);
        return entity;
    }

    public static TransactionEntity createIncomeTransactionEntity() {
        CategoryEntity incomeCategory = createIncomeCategoryEntity();
        return createTransactionEntity(
                1L,
                Type.INCOME,
                new BigDecimal("1000.00"),
                "Test income transaction",
                incomeCategory,
                LocalDate.now(),
                LocalDateTime.now().minusHours(1)
        );
    }

    public static TransactionEntity createExpenseTransactionEntity() {
        CategoryEntity expenseCategory = createExpenseCategoryEntity();
        return createTransactionEntity(
                2L,
                Type.EXPENSE,
                new BigDecimal("500.00"),
                "Test expense transaction",
                expenseCategory,
                LocalDate.now(),
                LocalDateTime.now().minusHours(1)
        );
    }

    public static TransactionEntity createTransactionEntityWithAmount(BigDecimal amount, CategoryEntity category) {
        return createTransactionEntity(
                1L,
                category.getType(),
                amount,
                "Test transaction",
                category,
                LocalDate.now(),
                LocalDateTime.now().minusHours(1)
        );
    }
}