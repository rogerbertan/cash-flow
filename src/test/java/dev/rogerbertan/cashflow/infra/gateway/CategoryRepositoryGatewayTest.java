package dev.rogerbertan.cashflow.infra.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.infra.mapper.CategoryEntityMapper;
import dev.rogerbertan.cashflow.infra.persistence.CategoryEntity;
import dev.rogerbertan.cashflow.infra.persistence.CategoryRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryRepositoryGatewayTest {

    @Mock private CategoryRepository categoryRepository;

    @Mock private CategoryEntityMapper entityMapper;

    @InjectMocks private CategoryRepositoryGateway gateway;

    // findAllCategories tests

    @Test
    void findAllCategories_ShouldReturnEmptyList_WhenNoCategoriesExist() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Category> result = gateway.findAllCategories();

        // Assert
        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findAll();
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void findAllCategories_ShouldReturnAllCategories_WhenCategoriesExist() {
        // Arrange
        CategoryEntity entity1 = TestDataFactory.createIncomeCategoryEntity();
        CategoryEntity entity2 = TestDataFactory.createExpenseCategoryEntity();
        Category category1 = TestDataFactory.createIncomeCategory();
        Category category2 = TestDataFactory.createExpenseCategory();

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));
        when(entityMapper.toDomain(entity1)).thenReturn(category1);
        when(entityMapper.toDomain(entity2)).thenReturn(category2);

        // Act
        List<Category> result = gateway.findAllCategories();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(category1, category2);
        verify(categoryRepository, times(1)).findAll();
        verify(entityMapper, times(1)).toDomain(entity1);
        verify(entityMapper, times(1)).toDomain(entity2);
    }

    @Test
    void findAllCategories_ShouldMapEntitiesToDomain_WhenCategoriesExist() {
        // Arrange
        CategoryEntity entity = TestDataFactory.createIncomeCategoryEntity();
        Category category = TestDataFactory.createIncomeCategory();

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(entity));
        when(entityMapper.toDomain(entity)).thenReturn(category);

        // Act
        List<Category> result = gateway.findAllCategories();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(category);
        verify(entityMapper, times(1)).toDomain(entity);
    }

    // findCategoryById tests

    @Test
    void findCategoryById_ShouldReturnCategory_WhenCategoryExists() {
        // Arrange
        Long categoryId = 1L;
        CategoryEntity entity = TestDataFactory.createIncomeCategoryEntity();
        Category category = TestDataFactory.createIncomeCategory();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(category);

        // Act
        Category result = gateway.findCategoryById(categoryId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(category);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(entityMapper, times(1)).toDomain(entity);
    }

    @Test
    void findCategoryById_ShouldReturnNull_WhenCategoryDoesNotExist() {
        // Arrange
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        Category result = gateway.findCategoryById(categoryId);

        // Assert
        assertThat(result).isNull();
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(entityMapper, never()).toDomain(any(CategoryEntity.class));
    }

    @Test
    void findCategoryById_ShouldMapEntityToDomain_WhenCategoryExists() {
        // Arrange
        Long categoryId = 1L;
        CategoryEntity entity = TestDataFactory.createIncomeCategoryEntity();
        Category category = TestDataFactory.createIncomeCategory();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(category);

        // Act
        Category result = gateway.findCategoryById(categoryId);

        // Assert
        assertThat(result.id()).isEqualTo(category.id());
        assertThat(result.name()).isEqualTo(category.name());
        assertThat(result.type()).isEqualTo(category.type());
        verify(entityMapper, times(1)).toDomain(entity);
    }

    // createCategory tests

    @Test
    void createCategory_ShouldSaveAndReturnCategory_WhenValidCategoryProvided() {
        // Arrange
        Category inputCategory = new Category(null, "Salary", Type.INCOME, LocalDateTime.now());
        CategoryEntity inputEntity =
                TestDataFactory.createCategoryEntity(
                        null, "Salary", Type.INCOME, LocalDateTime.now());
        CategoryEntity savedEntity = TestDataFactory.createIncomeCategoryEntity();
        Category savedCategory = TestDataFactory.createIncomeCategory();

        when(entityMapper.toEntity(inputCategory)).thenReturn(inputEntity);
        when(categoryRepository.save(inputEntity)).thenReturn(savedEntity);
        when(entityMapper.toDomain(savedEntity)).thenReturn(savedCategory);

        // Act
        Category result = gateway.createCategory(inputCategory);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(savedCategory);
        verify(entityMapper, times(1)).toEntity(inputCategory);
        verify(categoryRepository, times(1)).save(inputEntity);
        verify(entityMapper, times(1)).toDomain(savedEntity);
    }

    @Test
    void createCategory_ShouldMapDomainToEntity_WhenCreating() {
        // Arrange
        Category inputCategory = new Category(null, "Food", Type.EXPENSE, LocalDateTime.now());
        CategoryEntity inputEntity = TestDataFactory.createExpenseCategoryEntity();
        CategoryEntity savedEntity = TestDataFactory.createExpenseCategoryEntity();
        Category savedCategory = TestDataFactory.createExpenseCategory();

        when(entityMapper.toEntity(inputCategory)).thenReturn(inputEntity);
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(savedEntity);
        when(entityMapper.toDomain(savedEntity)).thenReturn(savedCategory);

        // Act
        gateway.createCategory(inputCategory);

        // Assert
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(entityMapper, times(1)).toEntity(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue()).isEqualTo(inputCategory);
    }

    @Test
    void createCategory_ShouldPreserveCreatedAtTimestamp_WhenSaving() {
        // Arrange
        LocalDateTime fixedTimestamp = LocalDateTime.now().minusDays(5);
        Category inputCategory = new Category(null, "Salary", Type.INCOME, fixedTimestamp);
        CategoryEntity inputEntity =
                TestDataFactory.createCategoryEntity(null, "Salary", Type.INCOME, fixedTimestamp);
        CategoryEntity savedEntity =
                TestDataFactory.createCategoryEntity(1L, "Salary", Type.INCOME, fixedTimestamp);
        Category savedCategory = new Category(1L, "Salary", Type.INCOME, fixedTimestamp);

        when(entityMapper.toEntity(inputCategory)).thenReturn(inputEntity);
        when(categoryRepository.save(inputEntity)).thenReturn(savedEntity);
        when(entityMapper.toDomain(savedEntity)).thenReturn(savedCategory);

        // Act
        Category result = gateway.createCategory(inputCategory);

        // Assert
        assertThat(result.createdAt()).isCloseTo(fixedTimestamp, within(1, ChronoUnit.SECONDS));
        verify(entityMapper, times(1)).toDomain(savedEntity);
    }

    // updateCategory tests

    @Test
    void updateCategory_ShouldSaveAndReturnCategory_WhenValidCategoryProvided() {
        // Arrange
        Category inputCategory =
                new Category(1L, "Updated Salary", Type.INCOME, LocalDateTime.now().minusDays(1));
        CategoryEntity inputEntity =
                TestDataFactory.createCategoryEntity(
                        1L, "Updated Salary", Type.INCOME, LocalDateTime.now().minusDays(1));
        CategoryEntity updatedEntity =
                TestDataFactory.createCategoryEntity(
                        1L, "Updated Salary", Type.INCOME, LocalDateTime.now().minusDays(1));
        Category updatedCategory =
                new Category(1L, "Updated Salary", Type.INCOME, LocalDateTime.now().minusDays(1));

        when(entityMapper.toEntity(inputCategory)).thenReturn(inputEntity);
        when(categoryRepository.save(inputEntity)).thenReturn(updatedEntity);
        when(entityMapper.toDomain(updatedEntity)).thenReturn(updatedCategory);

        // Act
        Category result = gateway.updateCategory(inputCategory);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Updated Salary");
        verify(entityMapper, times(1)).toEntity(inputCategory);
        verify(categoryRepository, times(1)).save(inputEntity);
        verify(entityMapper, times(1)).toDomain(updatedEntity);
    }

    @Test
    void updateCategory_ShouldPreserveCreatedAtTimestamp_WhenUpdating() {
        // Arrange
        LocalDateTime originalTimestamp = LocalDateTime.now().minusDays(10);
        Category inputCategory = new Category(1L, "Updated Name", Type.INCOME, originalTimestamp);
        CategoryEntity inputEntity =
                TestDataFactory.createCategoryEntity(
                        1L, "Updated Name", Type.INCOME, originalTimestamp);
        CategoryEntity updatedEntity =
                TestDataFactory.createCategoryEntity(
                        1L, "Updated Name", Type.INCOME, originalTimestamp);
        Category updatedCategory = new Category(1L, "Updated Name", Type.INCOME, originalTimestamp);

        when(entityMapper.toEntity(inputCategory)).thenReturn(inputEntity);
        when(categoryRepository.save(inputEntity)).thenReturn(updatedEntity);
        when(entityMapper.toDomain(updatedEntity)).thenReturn(updatedCategory);

        // Act
        Category result = gateway.updateCategory(inputCategory);

        // Assert
        assertThat(result.createdAt()).isCloseTo(originalTimestamp, within(1, ChronoUnit.SECONDS));
        verify(entityMapper, times(1)).toDomain(updatedEntity);
    }

    // deleteCategory tests

    @Test
    void deleteCategory_ShouldDelegateToRepository_WhenCalled() {
        // Arrange
        Long categoryId = 1L;
        doNothing().when(categoryRepository).deleteById(categoryId);

        // Act
        gateway.deleteCategory(categoryId);

        // Assert
        verify(categoryRepository, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }
}
