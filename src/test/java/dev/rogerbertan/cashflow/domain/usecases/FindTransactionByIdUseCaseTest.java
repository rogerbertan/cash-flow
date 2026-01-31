package dev.rogerbertan.cashflow.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.domain.usecases.transaction.FindTransactionByIdUseCase;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindTransactionByIdUseCaseTest {

    @Mock private TransactionGateway transactionGateway;

    @InjectMocks private FindTransactionByIdUseCase useCase;

    @Test
    void execute_ShouldReturnTransaction_WhenTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        Category category = TestDataFactory.createIncomeCategory();
        Transaction expectedTransaction =
                new Transaction(
                        transactionId,
                        Type.INCOME,
                        new BigDecimal("1000.00"),
                        "Salary payment",
                        category,
                        LocalDate.now(),
                        LocalDateTime.now());
        when(transactionGateway.findTransactionById(transactionId)).thenReturn(expectedTransaction);

        // Act
        Transaction result = useCase.execute(transactionId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedTransaction);
        verify(transactionGateway, times(1)).findTransactionById(transactionId);
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
    }
}
