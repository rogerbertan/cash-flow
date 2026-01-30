package dev.rogerbertan.cash_flow.domain.usecases;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.domain.entities.Transaction;
import dev.rogerbertan.cash_flow.domain.enums.Type;
import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cash_flow.domain.usecases.transaction.CreateTransactionUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseTest {

    @Mock
    private TransactionGateway transactionGateway;

    @InjectMocks
    private CreateTransactionUseCase useCase;

    @Test
    void execute_ShouldCreateTransaction_WhenValidTransactionProvided() {
        // Arrange
        Category category = TestDataFactory.createIncomeCategory();
        Transaction inputTransaction = new Transaction(
                null,
                Type.INCOME,
                new BigDecimal("1000.00"),
                "Salary payment",
                category,
                LocalDate.now(),
                LocalDateTime.now()
        );
        Transaction savedTransaction = new Transaction(
                1L,
                Type.INCOME,
                new BigDecimal("1000.00"),
                "Salary payment",
                category,
                LocalDate.now(),
                LocalDateTime.now()
        );
        when(transactionGateway.createTransaction(inputTransaction)).thenReturn(savedTransaction);

        // Act
        Transaction result = useCase.execute(inputTransaction);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(savedTransaction);
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(result.description()).isEqualTo("Salary payment");
    }

    @Test
    void execute_ShouldDelegateToGateway_WhenCalled() {
        // Arrange
        Transaction transaction = TestDataFactory.createIncomeTransaction();
        when(transactionGateway.createTransaction(transaction)).thenReturn(transaction);

        // Act
        useCase.execute(transaction);

        // Assert
        verify(transactionGateway, times(1)).createTransaction(transaction);
        verifyNoMoreInteractions(transactionGateway);
    }
}