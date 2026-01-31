package dev.rogerbertan.cashflow.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.domain.usecases.category.UpdateCategoryUseCase;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {

    @Mock private CategoryGateway categoryGateway;

    @InjectMocks private UpdateCategoryUseCase useCase;

    @Test
    void execute_ShouldUpdateCategory_WhenCategoryExists() {
        // Arrange
        LocalDateTime oldTimestamp = LocalDateTime.now().minusDays(1);
        Category existingCategory = new Category(1L, "Old Name", Type.INCOME, oldTimestamp);
        Category inputCategory = new Category(1L, "New Name", Type.INCOME, oldTimestamp);
        Category updatedCategory = new Category(1L, "New Name", Type.INCOME, LocalDateTime.now());

        when(categoryGateway.findCategoryById(1L)).thenReturn(existingCategory);
        when(categoryGateway.updateCategory(any(Category.class))).thenReturn(updatedCategory);

        // Act
        Category result = useCase.execute(inputCategory);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("New Name");
        assertThat(result.type()).isEqualTo(Type.INCOME);
        verify(categoryGateway, times(1)).findCategoryById(1L);
        verify(categoryGateway, times(1)).updateCategory(any(Category.class));
    }

    @Test
    void execute_ShouldThrowResourceNotFoundException_WhenCategoryNotFound() {
        // Arrange
        Category category = new Category(999L, "Test", Type.INCOME, LocalDateTime.now());
        when(categoryGateway.findCategoryById(999L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(category))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found")
                .hasMessageContaining("id: 999");

        verify(categoryGateway, times(1)).findCategoryById(999L);
        verify(categoryGateway, never()).updateCategory(any(Category.class));
    }

    @Test
    void execute_ShouldUpdateTimestamp_WhenCategoryExists() {
        // Arrange
        LocalDateTime oldTimestamp = LocalDateTime.now().minusDays(1);
        Category existingCategory = new Category(1L, "Salary", Type.INCOME, oldTimestamp);
        Category inputCategory = new Category(1L, "Salary Updated", Type.INCOME, oldTimestamp);

        when(categoryGateway.findCategoryById(1L)).thenReturn(existingCategory);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        when(categoryGateway.updateCategory(categoryCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Category result = useCase.execute(inputCategory);

        // Assert
        Category capturedCategory = categoryCaptor.getValue();
        assertThat(capturedCategory.createdAt()).isNotNull();
        assertThat(capturedCategory.createdAt())
                .isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.SECONDS));
        assertThat(capturedCategory.id()).isEqualTo(1L);
        assertThat(capturedCategory.name()).isEqualTo("Salary Updated");
        assertThat(capturedCategory.type()).isEqualTo(Type.INCOME);
    }
}
