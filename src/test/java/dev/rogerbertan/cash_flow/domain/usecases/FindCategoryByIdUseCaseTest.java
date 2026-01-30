package dev.rogerbertan.cash_flow.domain.usecases;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.domain.enums.Type;
import dev.rogerbertan.cash_flow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cash_flow.domain.usecases.category.FindCategoryByIdUseCase;
import dev.rogerbertan.cash_flow.infra.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindCategoryByIdUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private FindCategoryByIdUseCase useCase;

    @Test
    void execute_ShouldReturnCategory_WhenCategoryExists() {
        // Arrange
        Long categoryId = 1L;
        Category expectedCategory = new Category(
                categoryId,
                "Salary",
                Type.INCOME,
                LocalDateTime.now()
        );
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