package dev.rogerbertan.cashflow.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.domain.usecases.category.FindCategoryByIdUseCase;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindCategoryByIdUseCaseTest {

    @Mock private CategoryGateway categoryGateway;

    @InjectMocks private FindCategoryByIdUseCase useCase;

    @Test
    void execute_ShouldReturnCategory_WhenCategoryExists() {
        // Arrange
        Long categoryId = 1L;
        Category expectedCategory =
                new Category(categoryId, "Salary", Type.INCOME, LocalDateTime.now());
        when(categoryGateway.findCategoryById(categoryId)).thenReturn(expectedCategory);

        // Act
        Category result = useCase.execute(categoryId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedCategory);
        verify(categoryGateway, times(1)).findCategoryById(categoryId);
    }

    @Test
    void execute_ShouldThrowResourceNotFoundException_WhenCategoryNotFound() {
        // Arrange
        Long categoryId = 999L;
        when(categoryGateway.findCategoryById(categoryId)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found")
                .hasMessageContaining("id: 999");

        verify(categoryGateway, times(1)).findCategoryById(categoryId);
    }
}
