package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.TransactionGateway;
import dev.rogerbertan.budget_planner_clean_arch.infra.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindTransactionByIdUseCaseTest {

    @Mock
    private TransactionGateway transactionGateway;

    @InjectMocks
    private FindTransactionByIdUseCase useCase;

    @Test
    void execute_ShouldReturnTransaction_WhenTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        Category category = TestDataFactory.createIncomeCategory();
        Transaction expectedTransaction = new Transaction(
                transactionId,
                Type.INCOME,
                new BigDecimal("1000.00"),
                "Salary payment",
                category,
                LocalDate.now(),
                LocalDateTime.now()
        );
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