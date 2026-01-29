package dev.rogerbertan.cash_flow.domain.usecases;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.domain.entities.Transaction;
import dev.rogerbertan.cash_flow.domain.enums.Type;
import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cash_flow.infra.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTransactionUseCaseTest {

    @Mock
    private TransactionGateway transactionGateway;

    @InjectMocks
    private UpdateTransactionUseCase useCase;

    @Test
    void execute_ShouldThrowResourceNotFoundException_WhenTransactionNotFound() {
        // Arrange
        Category category = TestDataFactory.createIncomeCategory();
        Transaction transaction = new Transaction(
                999L,
                Type.INCOME,
                new BigDecimal("1000.00"),
                "Test",
                category,
                LocalDate.now(),
                LocalDateTime.now()
        );
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
        Transaction existingTransaction = new Transaction(
                1L,
                Type.INCOME,
                new BigDecimal("1000.00"),
                "Old description",
                category,
                LocalDate.now().minusDays(1),
                oldTimestamp
        );
        Transaction inputTransaction = new Transaction(
                1L,
                Type.INCOME,
                new BigDecimal("1500.00"),
                "New description",
                category,
                LocalDate.now(),
                oldTimestamp
        );

        when(transactionGateway.findTransactionById(1L)).thenReturn(existingTransaction);
        when(transactionGateway.updateTransaction(any(Transaction.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

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
        Transaction existingTransaction = new Transaction(
                1L,
                Type.INCOME,
                new BigDecimal("1000.00"),
                "Old description",
                category,
                LocalDate.now().minusDays(1),
                oldTimestamp
        );
        Transaction inputTransaction = new Transaction(
                1L,
                Type.EXPENSE,
                new BigDecimal("2000.00"),
                "Updated description",
                category,
                LocalDate.now(),
                oldTimestamp
        );

        when(transactionGateway.findTransactionById(1L)).thenReturn(existingTransaction);
        when(transactionGateway.updateTransaction(any(Transaction.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

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
        Transaction existingTransaction = new Transaction(
                1L,
                Type.INCOME,
                new BigDecimal("1000.00"),
                "Old description",
                category,
                LocalDate.now().minusDays(1),
                oldTimestamp
        );
        Transaction inputTransaction = new Transaction(
                1L,
                Type.INCOME,
                new BigDecimal("1500.00"),
                "New description",
                category,
                LocalDate.now(),
                oldTimestamp
        );
        Transaction updatedTransaction = new Transaction(
                1L,
                Type.INCOME,
                new BigDecimal("1500.00"),
                "New description",
                category,
                LocalDate.now(),
                LocalDateTime.now()
        );

        when(transactionGateway.findTransactionById(1L)).thenReturn(existingTransaction);
        when(transactionGateway.updateTransaction(any(Transaction.class))).thenReturn(updatedTransaction);

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
