package dev.rogerbertan.cash_flow.domain.usecases;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cash_flow.domain.usecases.category.DeleteCategoryUseCase;
import dev.rogerbertan.cash_flow.infra.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DeleteCategoryUseCase useCase;

    @Test
    void execute_ShouldDeleteCategory_WhenCategoryExists() {
        // Arrange
        Long categoryId = 1L;
        Category existingCategory = TestDataFactory.createIncomeCategory();
        when(categoryGateway.findCategoryById(categoryId)).thenReturn(existingCategory);
        doNothing().when(categoryGateway).deleteCategory(categoryId);

        // Act
        useCase.execute(categoryId);

        // Assert
        verify(categoryGateway, times(1)).findCategoryById(categoryId);
        verify(categoryGateway, times(1)).deleteCategory(categoryId);
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
        verify(categoryGateway, never()).deleteCategory(anyLong());
    }
}