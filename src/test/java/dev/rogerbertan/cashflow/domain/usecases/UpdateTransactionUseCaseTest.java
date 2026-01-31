package dev.rogerbertan.cashflow.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.domain.usecases.transaction.UpdateTransactionUseCase;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateTransactionUseCaseTest {

    @Mock private TransactionGateway transactionGateway;

    @InjectMocks private UpdateTransactionUseCase useCase;

    @Test
    void execute_ShouldThrowResourceNotFoundException_WhenTransactionNotFound() {
        // Arrange
        Category category = TestDataFactory.createIncomeCategory();
        Transaction transaction =
                new Transaction(
                        999L,
                        Type.INCOME,
                        new BigDecimal("1000.00"),
                        "Test",
                        category,
                        LocalDate.now(),
                        LocalDateTime.now());
        when(transactionGateway.findTransactionById(999L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(transaction))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaction not found")
                .hasMessageContaining("id: 999");

        verify(transactionGateway, times(1)).findTransactionById(999L);
    }

    @Test
    void execute_ShouldUpdateTimestamp_WhenTransactionExists() {
        // Arrange
        LocalDateTime oldTimestamp = LocalDateTime.now().minusDays(1);
        Category category = TestDataFactory.createIncomeCategory();
        Transaction existingTransaction =
                new Transaction(
                        1L,
                        Type.INCOME,
                        new BigDecimal("1000.00"),
                        "Old description",
                        category,
                        LocalDate.now().minusDays(1),
                        oldTimestamp);
        Transaction inputTransaction =
                new Transaction(
                        1L,
                        Type.INCOME,
                        new BigDecimal("1500.00"),
                        "New description",
                        category,
                        LocalDate.now(),
                        oldTimestamp);

        when(transactionGateway.findTransactionById(1L)).thenReturn(existingTransaction);
        when(transactionGateway.updateTransaction(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction result = useCase.execute(inputTransaction);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.createdAt())
                .isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.SECONDS));
        verify(transactionGateway, times(1)).findTransactionById(1L);
        verify(transactionGateway, times(1)).updateTransaction(any(Transaction.class));
    }

    @Test
    void execute_ShouldPreserveAllFieldsExceptTimestamp_WhenUpdating() {
        // Arrange
        LocalDateTime oldTimestamp = LocalDateTime.now().minusDays(1);
        Category category = TestDataFactory.createIncomeCategory();
        Transaction existingTransaction =
                new Transaction(
                        1L,
                        Type.INCOME,
                        new BigDecimal("1000.00"),
                        "Old description",
                        category,
                        LocalDate.now().minusDays(1),
                        oldTimestamp);
        Transaction inputTransaction =
                new Transaction(
                        1L,
                        Type.EXPENSE,
                        new BigDecimal("2000.00"),
                        "Updated description",
                        category,
                        LocalDate.now(),
                        oldTimestamp);

        when(transactionGateway.findTransactionById(1L)).thenReturn(existingTransaction);
        when(transactionGateway.updateTransaction(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction result = useCase.execute(inputTransaction);

        // Assert
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.type()).isEqualTo(Type.EXPENSE);
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(result.description()).isEqualTo("Updated description");
        assertThat(result.category()).isEqualTo(category);
        assertThat(result.transactionDate()).isEqualTo(LocalDate.now());
        verify(transactionGateway, times(1)).updateTransaction(any(Transaction.class));
    }

    @Test
    void execute_ShouldUpdateTransaction_WhenTransactionExists() {
        // Arrange
        LocalDateTime oldTimestamp = LocalDateTime.now().minusDays(1);
        Category category = TestDataFactory.createIncomeCategory();
        Transaction existingTransaction =
                new Transaction(
                        1L,
                        Type.INCOME,
                        new BigDecimal("1000.00"),
                        "Old description",
                        category,
                        LocalDate.now().minusDays(1),
                        oldTimestamp);
        Transaction inputTransaction =
                new Transaction(
                        1L,
                        Type.INCOME,
                        new BigDecimal("1500.00"),
                        "New description",
                        category,
                        LocalDate.now(),
                        oldTimestamp);
        Transaction updatedTransaction =
                new Transaction(
                        1L,
                        Type.INCOME,
                        new BigDecimal("1500.00"),
                        "New description",
                        category,
                        LocalDate.now(),
                        LocalDateTime.now());

        when(transactionGateway.findTransactionById(1L)).thenReturn(existingTransaction);
        when(transactionGateway.updateTransaction(any(Transaction.class)))
                .thenReturn(updatedTransaction);

        // Act
        Transaction result = useCase.execute(inputTransaction);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(result.description()).isEqualTo("New description");
        verify(transactionGateway, times(1)).findTransactionById(1L);
        verify(transactionGateway, times(1)).updateTransaction(any(Transaction.class));
    }
}
