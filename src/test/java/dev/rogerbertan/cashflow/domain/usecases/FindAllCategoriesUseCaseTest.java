package dev.rogerbertan.cashflow.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.domain.usecases.category.FindAllCategoriesUseCase;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindAllCategoriesUseCaseTest {

    @Mock private CategoryGateway categoryGateway;

    @InjectMocks private FindAllCategoriesUseCase useCase;

    @Test
    void execute_ShouldReturnAllCategories_WhenCategoriesExist() {
        // Arrange
        Category category1 = new Category(1L, "Salary", Type.INCOME, LocalDateTime.now());
        Category category2 = new Category(2L, "Food", Type.EXPENSE, LocalDateTime.now());
        Category category3 = new Category(3L, "Transport", Type.EXPENSE, LocalDateTime.now());
        List<Category> categories = Arrays.asList(category1, category2, category3);

        when(categoryGateway.findAllCategories()).thenReturn(categories);

        // Act
        List<Category> result = useCase.execute();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(category1, category2, category3);
        verify(categoryGateway, times(1)).findAllCategories();
    }

    @Test
    void execute_ShouldReturnEmptyList_WhenNoCategoriesExist() {
        // Arrange
        when(categoryGateway.findAllCategories()).thenReturn(Collections.emptyList());

        // Act
        List<Category> result = useCase.execute();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(categoryGateway, times(1)).findAllCategories();
    }
}
