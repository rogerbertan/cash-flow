package dev.rogerbertan.cashflow.infra.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySummary;
import dev.rogerbertan.cashflow.infra.exception.InvalidTransactionException;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;
import dev.rogerbertan.cashflow.infra.mapper.TransactionEntityMapper;
import dev.rogerbertan.cashflow.infra.persistence.CategoryEntity;
import dev.rogerbertan.cashflow.infra.persistence.CategoryRepository;
import dev.rogerbertan.cashflow.infra.persistence.TransactionEntity;
import dev.rogerbertan.cashflow.infra.persistence.TransactionRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryGatewayTest {

    @Mock private TransactionRepository transactionRepository;

    @Mock private CategoryRepository categoryRepository;

    @Mock private TransactionEntityMapper entityMapper;

    @InjectMocks private TransactionRepositoryGateway gateway;

    // findAllTransactions tests (Pagination)

    @Test
    void findAllTransactions_ShouldReturnPagedTransactions_WhenTransactionsExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        TransactionEntity entity1 = TestDataFactory.createIncomeTransactionEntity();
        TransactionEntity entity2 = TestDataFactory.createExpenseTransactionEntity();
        Transaction transaction1 = TestDataFactory.createIncomeTransaction();
        Transaction transaction2 = TestDataFactory.createExpenseTransaction();

        Page<TransactionEntity> entityPage =
                new PageImpl<>(Arrays.asList(entity1, entity2), pageable, 2);
        when(transactionRepository.findAll(pageable)).thenReturn(entityPage);
        when(entityMapper.toDomain(entity1)).thenReturn(transaction1);
        when(entityMapper.toDomain(entity2)).thenReturn(transaction2);

        // Act
        Page<Transaction> result = gateway.findAllTransactions(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);
        verify(transactionRepository, times(1)).findAll(pageable);
    }

    @Test
    void findAllTransactions_ShouldReturnEmptyPage_WhenNoTransactionsExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<TransactionEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(transactionRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<Transaction> result = gateway.findAllTransactions(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(transactionRepository, times(1)).findAll(pageable);
    }

    @Test
    void findAllTransactions_ShouldRespectPagination_WhenPageableProvided() {
        // Arrange
        Pageable pageable = PageRequest.of(2, 10);
        Page<TransactionEntity> entityPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(transactionRepository.findAll(pageable)).thenReturn(entityPage);

        // Act
        Page<Transaction> result = gateway.findAllTransactions(pageable);

        // Assert
        assertThat(result.getNumber()).isEqualTo(2);
        assertThat(result.getSize()).isEqualTo(10);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(transactionRepository).findAll(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(2);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
    }

    // findTransactionById tests

    @Test
    void findTransactionById_ShouldReturnTransaction_WhenTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        TransactionEntity entity = TestDataFactory.createIncomeTransactionEntity();
        Transaction transaction = TestDataFactory.createIncomeTransaction();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(transaction);

        // Act
        Transaction result = gateway.findTransactionById(transactionId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(transaction);
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(entityMapper, times(1)).toDomain(entity);
    }

    @Test
    void findTransactionById_ShouldReturnNull_WhenTransactionDoesNotExist() {
        // Arrange
        Long transactionId = 999L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act
        Transaction result = gateway.findTransactionById(transactionId);

        // Assert
        assertThat(result).isNull();
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(entityMapper, never()).toDomain(any(TransactionEntity.class));
    }

    // createTransaction tests (Success Cases)

    @Test
    void createTransaction_ShouldCreateTransaction_WhenValidIncomeProvided() {
        // Arrange
        Category incomeCategory = TestDataFactory.createIncomeCategory();
        Transaction inputTransaction = TestDataFactory.createIncomeTransaction();
        CategoryEntity categoryEntity = TestDataFactory.createIncomeCategoryEntity();
        TransactionEntity inputEntity = TestDataFactory.createIncomeTransactionEntity();
        TransactionEntity savedEntity = TestDataFactory.createIncomeTransactionEntity();
        Transaction savedTransaction = TestDataFactory.createIncomeTransaction();

        when(categoryRepository.findById(incomeCategory.id()))
                .thenReturn(Optional.of(categoryEntity));
        when(entityMapper.toEntity(inputTransaction)).thenReturn(inputEntity);
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(savedEntity);
        when(entityMapper.toDomain(savedEntity)).thenReturn(savedTransaction);

        // Act
        Transaction result = gateway.createTransaction(inputTransaction);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(savedTransaction);
        verify(categoryRepository, times(1)).findById(incomeCategory.id());
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    void createTransaction_ShouldCreateTransaction_WhenValidExpenseProvided() {
        // Arrange
        Category expenseCategory = TestDataFactory.createExpenseCategory();
        Transaction inputTransaction = TestDataFactory.createExpenseTransaction();
        CategoryEntity categoryEntity = TestDataFactory.createExpenseCategoryEntity();
        TransactionEntity inputEntity = TestDataFactory.createExpenseTransactionEntity();
        TransactionEntity savedEntity = TestDataFactory.createExpenseTransactionEntity();
        Transaction savedTransaction = TestDataFactory.createExpenseTransaction();

        when(categoryRepository.findById(expenseCategory.id()))
                .thenReturn(Optional.of(categoryEntity));
        when(entityMapper.toEntity(inputTransaction)).thenReturn(inputEntity);
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(savedEntity);
        when(entityMapper.toDomain(savedEntity)).thenReturn(savedTransaction);

        // Act
        Transaction result = gateway.createTransaction(inputTransaction);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(savedTransaction);
        verify(categoryRepository, times(1)).findById(expenseCategory.id());
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    void createTransaction_ShouldMapDomainToEntity_WhenCreating() {
        // Arrange
        Transaction inputTransaction = TestDataFactory.createIncomeTransaction();
        CategoryEntity categoryEntity = TestDataFactory.createIncomeCategoryEntity();
        TransactionEntity inputEntity = TestDataFactory.createIncomeTransactionEntity();
        TransactionEntity savedEntity = TestDataFactory.createIncomeTransactionEntity();
        Transaction savedTransaction = TestDataFactory.createIncomeTransaction();

        when(categoryRepository.findById(inputTransaction.category().id()))
                .thenReturn(Optional.of(categoryEntity));
        when(entityMapper.toEntity(inputTransaction)).thenReturn(inputEntity);
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(savedEntity);
        when(entityMapper.toDomain(savedEntity)).thenReturn(savedTransaction);

        // Act
        gateway.createTransaction(inputTransaction);

        // Assert
        verify(entityMapper, times(1)).toEntity(inputTransaction);
    }

    @Test
    void createTransaction_ShouldSetCategoryOnEntity_WhenCreating() {
        // Arrange
        Transaction inputTransaction = TestDataFactory.createIncomeTransaction();
        CategoryEntity categoryEntity = TestDataFactory.createIncomeCategoryEntity();
        TransactionEntity inputEntity = TestDataFactory.createIncomeTransactionEntity();
        TransactionEntity savedEntity = TestDataFactory.createIncomeTransactionEntity();
        Transaction savedTransaction = TestDataFactory.createIncomeTransaction();

        when(categoryRepository.findById(inputTransaction.category().id()))
                .thenReturn(Optional.of(categoryEntity));
        when(entityMapper.toEntity(inputTransaction)).thenReturn(inputEntity);
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(savedEntity);
        when(entityMapper.toDomain(savedEntity)).thenReturn(savedTransaction);

        // Act
        gateway.createTransaction(inputTransaction);

        // Assert
        ArgumentCaptor<TransactionEntity> entityCaptor =
                ArgumentCaptor.forClass(TransactionEntity.class);
        verify(transactionRepository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getCategory()).isEqualTo(categoryEntity);
    }

    // createTransaction tests (Validation Failures)

    @Test
    void createTransaction_ShouldThrowResourceNotFoundException_WhenCategoryDoesNotExist() {
        // Arrange
        Transaction inputTransaction = TestDataFactory.createIncomeTransaction();
        when(categoryRepository.findById(inputTransaction.category().id()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> gateway.createTransaction(inputTransaction))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category")
                .hasMessageContaining("id: " + inputTransaction.category().id());

        verify(categoryRepository, times(1)).findById(inputTransaction.category().id());
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    void createTransaction_ShouldThrowInvalidTransactionException_WhenAmountIsZero() {
        // Arrange
        Category category = TestDataFactory.createIncomeCategory();
        Transaction transactionWithZeroAmount =
                new Transaction(
                        null,
                        Type.INCOME,
                        BigDecimal.ZERO,
                        "Test",
                        category,
                        java.time.LocalDate.now(),
                        java.time.LocalDateTime.now());

        // Act & Assert
        assertThatThrownBy(() -> gateway.createTransaction(transactionWithZeroAmount))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Amount must be positive")
                .hasMessageContaining("0");

        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    void createTransaction_ShouldThrowInvalidTransactionException_WhenAmountIsNegative() {
        // Arrange
        Category category = TestDataFactory.createIncomeCategory();
        Transaction transactionWithNegativeAmount =
                new Transaction(
                        null,
                        Type.INCOME,
                        new BigDecimal("-100.00"),
                        "Test",
                        category,
                        java.time.LocalDate.now(),
                        java.time.LocalDateTime.now());

        // Act & Assert
        assertThatThrownBy(() -> gateway.createTransaction(transactionWithNegativeAmount))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Amount must be positive")
                .hasMessageContaining("-100");

        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    void
            createTransaction_ShouldThrowInvalidTransactionException_WhenIncomeTransactionHasExpenseCategory() {
        // Arrange
        Category expenseCategory = TestDataFactory.createExpenseCategory();
        Transaction incomeTransactionWithExpenseCategory =
                new Transaction(
                        null,
                        Type.INCOME,
                        new BigDecimal("1000.00"),
                        "Test",
                        expenseCategory,
                        java.time.LocalDate.now(),
                        java.time.LocalDateTime.now());
        CategoryEntity expenseCategoryEntity = TestDataFactory.createExpenseCategoryEntity();

        when(categoryRepository.findById(expenseCategory.id()))
                .thenReturn(Optional.of(expenseCategoryEntity));

        // Act & Assert
        assertThatThrownBy(() -> gateway.createTransaction(incomeTransactionWithExpenseCategory))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Transaction type must be equal to category type");

        verify(categoryRepository, times(1)).findById(expenseCategory.id());
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    void
            createTransaction_ShouldThrowInvalidTransactionException_WhenExpenseTransactionHasIncomeCategory() {
        // Arrange
        Category incomeCategory = TestDataFactory.createIncomeCategory();
        Transaction expenseTransactionWithIncomeCategory =
                new Transaction(
                        null,
                        Type.EXPENSE,
                        new BigDecimal("500.00"),
                        "Test",
                        incomeCategory,
                        java.time.LocalDate.now(),
                        java.time.LocalDateTime.now());
        CategoryEntity incomeCategoryEntity = TestDataFactory.createIncomeCategoryEntity();

        when(categoryRepository.findById(incomeCategory.id()))
                .thenReturn(Optional.of(incomeCategoryEntity));

        // Act & Assert
        assertThatThrownBy(() -> gateway.createTransaction(expenseTransactionWithIncomeCategory))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Transaction type must be equal to category type");

        verify(categoryRepository, times(1)).findById(incomeCategory.id());
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    // updateTransaction tests (Success Cases)

    @Test
    void updateTransaction_ShouldUpdateTransaction_WhenValidIncomeProvided() {
        // Arrange
        Transaction inputTransaction = TestDataFactory.createIncomeTransaction();
        CategoryEntity categoryEntity = TestDataFactory.createIncomeCategoryEntity();
        TransactionEntity inputEntity = TestDataFactory.createIncomeTransactionEntity();
        TransactionEntity updatedEntity = TestDataFactory.createIncomeTransactionEntity();
        Transaction updatedTransaction = TestDataFactory.createIncomeTransaction();

        when(categoryRepository.findById(inputTransaction.category().id()))
                .thenReturn(Optional.of(categoryEntity));
        when(entityMapper.toEntity(inputTransaction)).thenReturn(inputEntity);
        when(transactionRepository.save(inputEntity)).thenReturn(updatedEntity);
        when(entityMapper.toDomain(updatedEntity)).thenReturn(updatedTransaction);

        // Act
        Transaction result = gateway.updateTransaction(inputTransaction);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(updatedTransaction);
        verify(categoryRepository, times(1)).findById(inputTransaction.category().id());
        verify(transactionRepository, times(1)).save(inputEntity);
    }

    @Test
    void updateTransaction_ShouldUpdateTransaction_WhenValidExpenseProvided() {
        // Arrange
        Transaction inputTransaction = TestDataFactory.createExpenseTransaction();
        CategoryEntity categoryEntity = TestDataFactory.createExpenseCategoryEntity();
        TransactionEntity inputEntity = TestDataFactory.createExpenseTransactionEntity();
        TransactionEntity updatedEntity = TestDataFactory.createExpenseTransactionEntity();
        Transaction updatedTransaction = TestDataFactory.createExpenseTransaction();

        when(categoryRepository.findById(inputTransaction.category().id()))
                .thenReturn(Optional.of(categoryEntity));
        when(entityMapper.toEntity(inputTransaction)).thenReturn(inputEntity);
        when(transactionRepository.save(inputEntity)).thenReturn(updatedEntity);
        when(entityMapper.toDomain(updatedEntity)).thenReturn(updatedTransaction);

        // Act
        Transaction result = gateway.updateTransaction(inputTransaction);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(updatedTransaction);
        verify(categoryRepository, times(1)).findById(inputTransaction.category().id());
        verify(transactionRepository, times(1)).save(inputEntity);
    }

    // updateTransaction tests (Validation Failures)

    @Test
    void updateTransaction_ShouldThrowResourceNotFoundException_WhenCategoryDoesNotExist() {
        // Arrange
        Transaction inputTransaction = TestDataFactory.createIncomeTransaction();
        when(categoryRepository.findById(inputTransaction.category().id()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> gateway.updateTransaction(inputTransaction))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category")
                .hasMessageContaining("id: " + inputTransaction.category().id());

        verify(categoryRepository, times(1)).findById(inputTransaction.category().id());
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    void updateTransaction_ShouldThrowInvalidTransactionException_WhenAmountIsZero() {
        // Arrange
        Category category = TestDataFactory.createIncomeCategory();
        Transaction transactionWithZeroAmount =
                new Transaction(
                        1L,
                        Type.INCOME,
                        BigDecimal.ZERO,
                        "Test",
                        category,
                        java.time.LocalDate.now(),
                        java.time.LocalDateTime.now());

        // Act & Assert
        assertThatThrownBy(() -> gateway.updateTransaction(transactionWithZeroAmount))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Amount must be positive");

        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    void updateTransaction_ShouldThrowInvalidTransactionException_WhenAmountIsNegative() {
        // Arrange
        Category category = TestDataFactory.createIncomeCategory();
        Transaction transactionWithNegativeAmount =
                new Transaction(
                        1L,
                        Type.INCOME,
                        new BigDecimal("-50.00"),
                        "Test",
                        category,
                        java.time.LocalDate.now(),
                        java.time.LocalDateTime.now());

        // Act & Assert
        assertThatThrownBy(() -> gateway.updateTransaction(transactionWithNegativeAmount))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Amount must be positive");

        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    void updateTransaction_ShouldThrowInvalidTransactionException_WhenTypeMismatch() {
        // Arrange
        Category expenseCategory = TestDataFactory.createExpenseCategory();
        Transaction incomeTransactionWithExpenseCategory =
                new Transaction(
                        1L,
                        Type.INCOME,
                        new BigDecimal("1000.00"),
                        "Test",
                        expenseCategory,
                        java.time.LocalDate.now(),
                        java.time.LocalDateTime.now());
        CategoryEntity expenseCategoryEntity = TestDataFactory.createExpenseCategoryEntity();

        when(categoryRepository.findById(expenseCategory.id()))
                .thenReturn(Optional.of(expenseCategoryEntity));

        // Act & Assert
        assertThatThrownBy(() -> gateway.updateTransaction(incomeTransactionWithExpenseCategory))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Transaction type must be equal to category type");

        verify(categoryRepository, times(1)).findById(expenseCategory.id());
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    // deleteTransaction tests

    @Test
    void deleteTransaction_ShouldDelegateToRepository_WhenCalled() {
        // Arrange
        Long transactionId = 1L;
        doNothing().when(transactionRepository).deleteById(transactionId);

        // Act
        gateway.deleteTransaction(transactionId);

        // Assert
        verify(transactionRepository, times(1)).deleteById(transactionId);
        verifyNoMoreInteractions(transactionRepository);
    }

    // getTotalIncome tests

    @Test
    void getTotalIncome_ShouldReturnTotal_WhenIncomeTransactionsExist() {
        // Arrange
        BigDecimal expectedTotal = new BigDecimal("5000.00");
        when(transactionRepository.sumAmountByType(Type.INCOME)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = gateway.getTotalIncome();

        // Assert
        assertThat(result).isEqualByComparingTo(expectedTotal);
        verify(transactionRepository, times(1)).sumAmountByType(Type.INCOME);
    }

    @Test
    void getTotalIncome_ShouldDelegateToRepository_WhenCalled() {
        // Arrange
        when(transactionRepository.sumAmountByType(Type.INCOME)).thenReturn(BigDecimal.ZERO);

        // Act
        gateway.getTotalIncome();

        // Assert
        ArgumentCaptor<Type> typeCaptor = ArgumentCaptor.forClass(Type.class);
        verify(transactionRepository).sumAmountByType(typeCaptor.capture());
        assertThat(typeCaptor.getValue()).isEqualTo(Type.INCOME);
    }

    // getTotalExpense tests

    @Test
    void getTotalExpense_ShouldReturnTotal_WhenExpenseTransactionsExist() {
        // Arrange
        BigDecimal expectedTotal = new BigDecimal("2000.00");
        when(transactionRepository.sumAmountByType(Type.EXPENSE)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = gateway.getTotalExpense();

        // Assert
        assertThat(result).isEqualByComparingTo(expectedTotal);
        verify(transactionRepository, times(1)).sumAmountByType(Type.EXPENSE);
    }

    @Test
    void getTotalExpense_ShouldDelegateToRepository_WhenCalled() {
        // Arrange
        when(transactionRepository.sumAmountByType(Type.EXPENSE)).thenReturn(BigDecimal.ZERO);

        // Act
        gateway.getTotalExpense();

        // Assert
        ArgumentCaptor<Type> typeCaptor = ArgumentCaptor.forClass(Type.class);
        verify(transactionRepository).sumAmountByType(typeCaptor.capture());
        assertThat(typeCaptor.getValue()).isEqualTo(Type.EXPENSE);
    }

    // getMonthlyIncome tests

    @Test
    void getMonthlyIncome_ShouldReturnTotal_WhenIncomeTransactionsExistForMonth() {
        // Arrange
        int month = 3;
        int year = 2024;
        BigDecimal expectedTotal = new BigDecimal("3000.00");
        when(transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.INCOME))
                .thenReturn(expectedTotal);

        // Act
        BigDecimal result = gateway.getMonthlyIncome(month, year);

        // Assert
        assertThat(result).isEqualByComparingTo(expectedTotal);
        verify(transactionRepository, times(1))
                .sumAmountByMonthAndYearAndType(month, year, Type.INCOME);
    }

    @Test
    void getMonthlyIncome_ShouldPassCorrectParameters_WhenCalled() {
        // Arrange
        int month = 5;
        int year = 2025;
        when(transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.INCOME))
                .thenReturn(BigDecimal.ZERO);

        // Act
        gateway.getMonthlyIncome(month, year);

        // Assert
        ArgumentCaptor<Integer> monthCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> yearCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Type> typeCaptor = ArgumentCaptor.forClass(Type.class);
        verify(transactionRepository)
                .sumAmountByMonthAndYearAndType(
                        monthCaptor.capture(), yearCaptor.capture(), typeCaptor.capture());
        assertThat(monthCaptor.getValue()).isEqualTo(5);
        assertThat(yearCaptor.getValue()).isEqualTo(2025);
        assertThat(typeCaptor.getValue()).isEqualTo(Type.INCOME);
    }

    // getMonthlyExpense tests

    @Test
    void getMonthlyExpense_ShouldReturnTotal_WhenExpenseTransactionsExistForMonth() {
        // Arrange
        int month = 6;
        int year = 2024;
        BigDecimal expectedTotal = new BigDecimal("1500.00");
        when(transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.EXPENSE))
                .thenReturn(expectedTotal);

        // Act
        BigDecimal result = gateway.getMonthlyExpense(month, year);

        // Assert
        assertThat(result).isEqualByComparingTo(expectedTotal);
        verify(transactionRepository, times(1))
                .sumAmountByMonthAndYearAndType(month, year, Type.EXPENSE);
    }

    @Test
    void getMonthlyExpense_ShouldPassCorrectParameters_WhenCalled() {
        // Arrange
        int month = 12;
        int year = 2026;
        when(transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.EXPENSE))
                .thenReturn(BigDecimal.ZERO);

        // Act
        gateway.getMonthlyExpense(month, year);

        // Assert
        ArgumentCaptor<Integer> monthCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> yearCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Type> typeCaptor = ArgumentCaptor.forClass(Type.class);
        verify(transactionRepository)
                .sumAmountByMonthAndYearAndType(
                        monthCaptor.capture(), yearCaptor.capture(), typeCaptor.capture());
        assertThat(monthCaptor.getValue()).isEqualTo(12);
        assertThat(yearCaptor.getValue()).isEqualTo(2026);
        assertThat(typeCaptor.getValue()).isEqualTo(Type.EXPENSE);
    }

    // getCategorySummaries tests

    @Test
    void getCategorySummaries_ShouldReturnSummaries_WhenTransactionsExist() {
        // Arrange
        int month = 8;
        int year = 2024;
        List<CategorySummary> expectedSummaries =
                Arrays.asList(
                        new CategorySummary("Salary", new BigDecimal("3000.00"), BigDecimal.ZERO),
                        new CategorySummary("Food", BigDecimal.ZERO, new BigDecimal("800.00")));
        when(transactionRepository.findCategorySummariesByMonthAndYear(month, year))
                .thenReturn(expectedSummaries);

        // Act
        List<CategorySummary> result = gateway.getCategorySummaries(month, year);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedSummaries);
        verify(transactionRepository, times(1)).findCategorySummariesByMonthAndYear(month, year);
    }

    @Test
    void getCategorySummaries_ShouldPassCorrectParameters_WhenCalled() {
        // Arrange
        int month = 11;
        int year = 2025;
        when(transactionRepository.findCategorySummariesByMonthAndYear(month, year))
                .thenReturn(Collections.emptyList());

        // Act
        gateway.getCategorySummaries(month, year);

        // Assert
        ArgumentCaptor<Integer> monthCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> yearCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(transactionRepository)
                .findCategorySummariesByMonthAndYear(monthCaptor.capture(), yearCaptor.capture());
        assertThat(monthCaptor.getValue()).isEqualTo(11);
        assertThat(yearCaptor.getValue()).isEqualTo(2025);
    }
}
