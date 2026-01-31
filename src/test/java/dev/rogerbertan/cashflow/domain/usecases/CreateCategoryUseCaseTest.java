package dev.rogerbertan.cashflow.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.domain.usecases.category.CreateCategoryUseCase;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @Mock private CategoryGateway categoryGateway;

    @InjectMocks private CreateCategoryUseCase useCase;

    @Test
    void execute_ShouldCreateCategory_WhenValidCategoryProvided() {
        // Arrange
        Category inputCategory = new Category(null, "Salary", Type.INCOME, LocalDateTime.now());
        Category savedCategory = new Category(1L, "Salary", Type.INCOME, LocalDateTime.now());
        when(categoryGateway.createCategory(inputCategory)).thenReturn(savedCategory);

        // Act
        Category result = useCase.execute(inputCategory);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(savedCategory);
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Salary");
        assertThat(result.type()).isEqualTo(Type.INCOME);
    }

    @Test
    void execute_ShouldDelegateToGateway_WhenCalled() {
        // Arrange
        Category category = TestDataFactory.createIncomeCategory();
        when(categoryGateway.createCategory(category)).thenReturn(category);

        // Act
        useCase.execute(category);

        // Assert
        verify(categoryGateway, times(1)).createCategory(category);
        verifyNoMoreInteractions(categoryGateway);
    }
}
