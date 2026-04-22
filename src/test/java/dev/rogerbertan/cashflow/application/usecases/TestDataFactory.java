package dev.rogerbertan.cashflow.domain.usecases;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.valueobjects.Balance;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySuggestion;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySummary;
import dev.rogerbertan.cashflow.domain.valueobjects.MonthlySummary;
import dev.rogerbertan.cashflow.infra.dto.*;
import dev.rogerbertan.cashflow.infra.persistence.CategoryEntity;
import dev.rogerbertan.cashflow.infra.persistence.TransactionEntity;
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
                LocalDateTime.now());
    }

    public static Transaction createIncomeTransaction() {
        return createTransaction(1L, createIncomeCategory(), new BigDecimal("1000.00"));
    }

    public static Transaction createExpenseTransaction() {
        return createTransaction(2L, createExpenseCategory(), new BigDecimal("500.00"));
    }

    // JPA Entity creation methods for gateway tests

    public static CategoryEntity createCategoryEntity(
            Long id, String name, Type type, LocalDateTime createdAt) {
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
                LocalDateTime.now().minusDays(1));
    }

    public static TransactionEntity createTransactionEntity(
            Long id,
            Type type,
            BigDecimal amount,
            String description,
            CategoryEntity category,
            LocalDate transactionDate,
            LocalDateTime createdAt) {
        TransactionEntity entity =
                new TransactionEntity(type, amount, description, category, transactionDate);
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
                LocalDateTime.now().minusHours(1));
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
                LocalDateTime.now().minusHours(1));
    }

    public static TransactionEntity createTransactionEntityWithAmount(
            BigDecimal amount, CategoryEntity category) {
        return createTransactionEntity(
                1L,
                category.getType(),
                amount,
                "Test transaction",
                category,
                LocalDate.now(),
                LocalDateTime.now().minusHours(1));
    }

    // DTO creation methods for controller tests

    // Category Request DTOs
    public static CategoryCreateRequest createCategoryCreateRequest(String name, Type type) {
        return new CategoryCreateRequest(name, type);
    }

    public static CategoryCreateRequest createIncomeCategoryCreateRequest() {
        return createCategoryCreateRequest("Salary", Type.INCOME);
    }

    public static CategoryCreateRequest createExpenseCategoryCreateRequest() {
        return createCategoryCreateRequest("Food", Type.EXPENSE);
    }

    public static CategoryUpdateRequest createCategoryUpdateRequest(
            Long id, String name, Type type, LocalDateTime createdAt) {
        return new CategoryUpdateRequest(id, name, type, createdAt);
    }

    public static CategoryUpdateRequest createCategoryUpdateRequest(String name, Type type) {
        return createCategoryUpdateRequest(1L, name, type, LocalDateTime.now());
    }

    // Category Response DTOs
    public static CategoryResponse createCategoryResponse(Long id, String name, Type type) {
        return new CategoryResponse(id, name, type);
    }

    public static CategoryResponse createIncomeCategoryResponse() {
        return createCategoryResponse(1L, "Salary", Type.INCOME);
    }

    public static CategoryResponse createExpenseCategoryResponse() {
        return createCategoryResponse(2L, "Food", Type.EXPENSE);
    }

    // Transaction Request DTOs
    public static TransactionCreateRequest createTransactionCreateRequest(
            Type type,
            BigDecimal amount,
            String description,
            Long categoryId,
            LocalDate transactionDate) {
        return new TransactionCreateRequest(type, amount, description, categoryId, transactionDate);
    }

    public static TransactionCreateRequest createIncomeTransactionCreateRequest() {
        return createTransactionCreateRequest(
                Type.INCOME,
                new BigDecimal("1000.00"),
                "Test income transaction",
                1L,
                LocalDate.now());
    }

    public static TransactionCreateRequest createExpenseTransactionCreateRequest() {
        return createTransactionCreateRequest(
                Type.EXPENSE,
                new BigDecimal("500.00"),
                "Test expense transaction",
                2L,
                LocalDate.now());
    }

    public static TransactionUpdateRequest createTransactionUpdateRequest(
            Long id,
            Type type,
            BigDecimal amount,
            String description,
            Category category,
            LocalDate transactionDate,
            LocalDateTime createdAt) {
        return new TransactionUpdateRequest(
                id, type, amount, description, category, transactionDate, createdAt);
    }

    public static TransactionUpdateRequest createTransactionUpdateRequest(
            Type type, BigDecimal amount, String description, Category category) {
        return createTransactionUpdateRequest(
                1L, type, amount, description, category, LocalDate.now(), LocalDateTime.now());
    }

    // Transaction Response DTOs
    public static TransactionResponse createTransactionResponse(
            Long id,
            Type type,
            BigDecimal amount,
            String description,
            Long categoryId,
            LocalDate transactionDate,
            LocalDateTime createdAt) {
        return new TransactionResponse(
                id, type, amount, description, categoryId, transactionDate, createdAt);
    }

    public static TransactionResponse createIncomeTransactionResponse() {
        return createTransactionResponse(
                1L,
                Type.INCOME,
                new BigDecimal("1000.00"),
                "Test income transaction",
                1L,
                LocalDate.now(),
                LocalDateTime.now().minusHours(1));
    }

    public static TransactionResponse createExpenseTransactionResponse() {
        return createTransactionResponse(
                2L,
                Type.EXPENSE,
                new BigDecimal("500.00"),
                "Test expense transaction",
                2L,
                LocalDate.now(),
                LocalDateTime.now().minusHours(1));
    }

    // Balance Value Object and Response DTO
    public static Balance createBalance(BigDecimal balance) {
        return new Balance(balance);
    }

    public static Balance createPositiveBalance() {
        return createBalance(new BigDecimal("1000.00"));
    }

    public static BalanceResponse createBalanceResponse(BigDecimal balance) {
        return new BalanceResponse(balance);
    }

    public static BalanceResponse createPositiveBalanceResponse() {
        return createBalanceResponse(new BigDecimal("1000.00"));
    }

    // Monthly Summary Value Object and Response DTO
    public static MonthlySummary createMonthlySummary(
            BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netBalance) {
        return new MonthlySummary(totalIncome, totalExpense, netBalance);
    }

    public static MonthlySummary createDefaultMonthlySummary() {
        return createMonthlySummary(
                new BigDecimal("5000.00"), new BigDecimal("3000.00"), new BigDecimal("2000.00"));
    }

    public static MonthlySummaryResponse createMonthlySummaryResponse(
            BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netBalance) {
        return new MonthlySummaryResponse(totalIncome, totalExpense, netBalance);
    }

    public static MonthlySummaryResponse createDefaultMonthlySummaryResponse() {
        return createMonthlySummaryResponse(
                new BigDecimal("5000.00"), new BigDecimal("3000.00"), new BigDecimal("2000.00"));
    }

    // Category Summary Value Object and Response DTO
    public static CategorySummary createCategorySummary(
            String categoryName, BigDecimal totalIncome, BigDecimal totalExpense) {
        return new CategorySummary(categoryName, totalIncome, totalExpense);
    }

    public static CategorySummary createIncomeCategorySummary() {
        return createCategorySummary("Salary", new BigDecimal("5000.00"), BigDecimal.ZERO);
    }

    public static CategorySummary createExpenseCategorySummary() {
        return createCategorySummary("Food", BigDecimal.ZERO, new BigDecimal("3000.00"));
    }

    public static CategoriesSummaryResponse createCategoriesSummaryResponse(
            String category, BigDecimal totalIncome, BigDecimal totalExpense) {
        return new CategoriesSummaryResponse(category, totalIncome, totalExpense);
    }

    public static CategoriesSummaryResponse createIncomeCategoriesSummaryResponse() {
        return createCategoriesSummaryResponse(
                "Salary", new BigDecimal("5000.00"), BigDecimal.ZERO);
    }

    public static CategoriesSummaryResponse createExpenseCategoriesSummaryResponse() {
        return createCategoriesSummaryResponse("Food", BigDecimal.ZERO, new BigDecimal("3000.00"));
    }

    // Category Suggestion Value Object
    public static CategorySuggestion createCategorySuggestion(
            Category category, String confidence, String rawResponse) {
        return new CategorySuggestion(category, confidence, rawResponse);
    }

    public static CategorySuggestion createHighConfidenceSuggestion() {
        return createCategorySuggestion(createExpenseCategory(), "high", "Food");
    }

    public static CategorySuggestion createLowConfidenceSuggestion() {
        return createCategorySuggestion(null, "low", "UnknownCategory");
    }

    public static CategorySuggestion createDisabledSuggestion() {
        return createCategorySuggestion(null, "disabled", "AI categorization is disabled");
    }

    // Category Suggestion Request DTO
    public static CategorySuggestionRequest createCategorySuggestionRequest(
            String description, Type type) {
        return new CategorySuggestionRequest(description, type);
    }

    public static CategorySuggestionRequest createExpenseSuggestionRequest() {
        return createCategorySuggestionRequest("grocery shopping", Type.EXPENSE);
    }

    public static CategorySuggestionRequest createIncomeSuggestionRequest() {
        return createCategorySuggestionRequest("monthly salary", Type.INCOME);
    }

    // Category Suggestion Response DTO
    public static CategorySuggestionResponse createCategorySuggestionResponse(
            CategoryResponse category, String confidence, String message) {
        return new CategorySuggestionResponse(category, confidence, message);
    }

    public static CategorySuggestionResponse createSuccessfulSuggestionResponse() {
        return createCategorySuggestionResponse(
                createExpenseCategoryResponse(), "high", "Category suggestion successful");
    }

    public static CategorySuggestionResponse createNoMatchSuggestionResponse() {
        return createCategorySuggestionResponse(
                null, "low", "No matching category found for this transaction");
    }
}
