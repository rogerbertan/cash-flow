package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;

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
}