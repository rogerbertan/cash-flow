package dev.rogerbertan.cashflow.domain.usecases;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.domain.usecases.transaction.DeleteTransactionUseCase;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteTransactionUseCaseTest {

    @Mock private TransactionGateway transactionGateway;

    @InjectMocks private DeleteTransactionUseCase useCase;

    @Test
    void execute_ShouldDeleteTransaction_WhenTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        Transaction existingTransaction = TestDataFactory.createIncomeTransaction();
        when(transactionGateway.findTransactionById(transactionId)).thenReturn(existingTransaction);
        doNothing().when(transactionGateway).deleteTransaction(transactionId);

        // Act
        useCase.execute(transactionId);

        // Assert
        verify(transactionGateway, times(1)).findTransactionById(transactionId);
        verify(transactionGateway, times(1)).deleteTransaction(transactionId);
    }

    @Test
    void execute_ShouldThrowResourceNotFoundException_WhenTransactionNotFound() {
        // Arrange
        Long transactionId = 999L;
        when(transactionGateway.findTransactionById(transactionId)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(transactionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaction not found")
                .hasMessageContaining("id: 999");

        verify(transactionGateway, times(1)).findTransactionById(transactionId);
        verify(transactionGateway, never()).deleteTransaction(anyLong());
    }
}
