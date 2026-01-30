package dev.rogerbertan.cash_flow.domain.usecases;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.domain.enums.Type;
import dev.rogerbertan.cash_flow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cash_flow.domain.usecases.category.CreateCategoryUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private CreateCategoryUseCase useCase;

    @Test
    void execute_ShouldCreateCategory_WhenValidCategoryProvided() {
        // Arrange
        Category inputCategory = new Category(
                null,
                "Salary",
                Type.INCOME,
                LocalDateTime.now()
        );
        Category savedCategory = new Category(
                1L,
                "Salary",
                Type.INCOME,
                LocalDateTime.now()
        );
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
