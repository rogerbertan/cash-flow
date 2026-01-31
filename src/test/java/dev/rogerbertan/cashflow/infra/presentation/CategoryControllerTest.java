package dev.rogerbertan.cashflow.infra.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.usecases.*;
import dev.rogerbertan.cashflow.domain.usecases.category.*;
import dev.rogerbertan.cashflow.infra.dto.CategoryCreateRequest;
import dev.rogerbertan.cashflow.infra.dto.CategoryResponse;
import dev.rogerbertan.cashflow.infra.dto.CategoryUpdateRequest;
import dev.rogerbertan.cashflow.infra.mapper.CategoryCreateMapper;
import dev.rogerbertan.cashflow.infra.mapper.CategoryResponseMapper;
import dev.rogerbertan.cashflow.infra.mapper.CategoryUpdateRequestMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock private FindAllCategoriesUseCase findAllCategoriesUseCase;

    @Mock private FindCategoryByIdUseCase findCategoryByIdUseCase;

    @Mock private CreateCategoryUseCase createCategoryUseCase;

    @Mock private UpdateCategoryUseCase updateCategoryUseCase;

    @Mock private DeleteCategoryUseCase deleteCategoryUseCase;

    @Mock private CategoryResponseMapper categoryResponseMapper;

    @Mock private CategoryCreateMapper categoryCreateMapper;

    @Mock private CategoryUpdateRequestMapper categoryUpdateRequestMapper;

    @InjectMocks private CategoryController controller;

    // getAllCategories tests

    @Test
    void getAllCategories_ShouldReturnEmptyList_WhenNoCategoriesExist() {
        // Arrange
        when(findAllCategoriesUseCase.execute()).thenReturn(Collections.emptyList());
        when(categoryResponseMapper.toListDTO(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<CategoryResponse>> result = controller.getAllCategories();

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEmpty();
        verify(findAllCategoriesUseCase, times(1)).execute();
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories_WhenCategoriesExist() {
        // Arrange
        Category category1 = TestDataFactory.createIncomeCategory();
        Category category2 = TestDataFactory.createExpenseCategory();
        CategoryResponse response1 = TestDataFactory.createIncomeCategoryResponse();
        CategoryResponse response2 = TestDataFactory.createExpenseCategoryResponse();

        when(findAllCategoriesUseCase.execute()).thenReturn(List.of(category1, category2));
        when(categoryResponseMapper.toListDTO(List.of(category1, category2)))
                .thenReturn(List.of(response1, response2));

        // Act
        ResponseEntity<List<CategoryResponse>> result = controller.getAllCategories();

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(2);
        assertThat(result.getBody()).containsExactly(response1, response2);
        verify(findAllCategoriesUseCase, times(1)).execute();
        verify(categoryResponseMapper, times(1)).toListDTO(List.of(category1, category2));
    }

    @Test
    void getAllCategories_ShouldDelegateToUseCase_WhenCalled() {
        // Arrange
        when(findAllCategoriesUseCase.execute()).thenReturn(Collections.emptyList());
        when(categoryResponseMapper.toListDTO(any())).thenReturn(Collections.emptyList());

        // Act
        controller.getAllCategories();

        // Assert
        verify(findAllCategoriesUseCase, times(1)).execute();
        verifyNoMoreInteractions(findAllCategoriesUseCase);
    }

    // getCategoryById tests

    @Test
    void getCategoryById_ShouldReturnCategory_WhenCategoryExists() {
        // Arrange
        Long categoryId = 1L;
        Category category = TestDataFactory.createIncomeCategory();
        CategoryResponse response = TestDataFactory.createIncomeCategoryResponse();

        when(findCategoryByIdUseCase.execute(categoryId)).thenReturn(category);
        when(categoryResponseMapper.toDTO(category)).thenReturn(response);

        // Act
        ResponseEntity<CategoryResponse> result = controller.getCategoryById(categoryId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().id()).isEqualTo(1L);
        assertThat(result.getBody().name()).isEqualTo("Salary");
        assertThat(result.getBody().type()).isEqualTo(Type.INCOME);
    }

    @Test
    void getCategoryById_ShouldCallMapperOnce_WhenCategoryExists() {
        // Arrange
        Long categoryId = 1L;
        Category category = TestDataFactory.createIncomeCategory();
        CategoryResponse response = TestDataFactory.createIncomeCategoryResponse();

        when(findCategoryByIdUseCase.execute(categoryId)).thenReturn(category);
        when(categoryResponseMapper.toDTO(category)).thenReturn(response);

        // Act
        controller.getCategoryById(categoryId);

        // Assert
        verify(categoryResponseMapper, times(1)).toDTO(category);
    }

    @Test
    void getCategoryById_ShouldDelegateToUseCase_WithCorrectId() {
        // Arrange
        Long categoryId = 123L;
        Category category = TestDataFactory.createIncomeCategory();
        CategoryResponse response = TestDataFactory.createIncomeCategoryResponse();

        when(findCategoryByIdUseCase.execute(categoryId)).thenReturn(category);
        when(categoryResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        controller.getCategoryById(categoryId);

        // Assert
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(findCategoryByIdUseCase, times(1)).execute(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(123L);
    }

    // createCategory tests

    @Test
    void createCategory_ShouldReturnCreatedCategory_WhenValidRequestProvided() {
        // Arrange
        CategoryCreateRequest request = TestDataFactory.createIncomeCategoryCreateRequest();
        Category inputCategory = TestDataFactory.createCategory(null, "Salary", Type.INCOME);
        Category savedCategory = TestDataFactory.createIncomeCategory();
        CategoryResponse response = TestDataFactory.createIncomeCategoryResponse();

        when(categoryCreateMapper.toEntity(request)).thenReturn(inputCategory);
        when(createCategoryUseCase.execute(inputCategory)).thenReturn(savedCategory);
        when(categoryResponseMapper.toDTO(savedCategory)).thenReturn(response);

        // Act
        ResponseEntity<CategoryResponse> result = controller.createCategory(request);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().id()).isEqualTo(1L);
        assertThat(result.getBody().name()).isEqualTo("Salary");
        assertThat(result.getBody().type()).isEqualTo(Type.INCOME);
    }

    @Test
    void createCategory_ShouldReturn201Status_WhenCategoryCreated() {
        // Arrange
        CategoryCreateRequest request = TestDataFactory.createIncomeCategoryCreateRequest();
        Category inputCategory = TestDataFactory.createCategory(null, "Salary", Type.INCOME);
        Category savedCategory = TestDataFactory.createIncomeCategory();
        CategoryResponse response = TestDataFactory.createIncomeCategoryResponse();

        when(categoryCreateMapper.toEntity(request)).thenReturn(inputCategory);
        when(createCategoryUseCase.execute(any())).thenReturn(savedCategory);
        when(categoryResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        ResponseEntity<CategoryResponse> result = controller.createCategory(request);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void createCategory_ShouldMapRequestToDomain_WhenCreating() {
        // Arrange
        CategoryCreateRequest request = TestDataFactory.createIncomeCategoryCreateRequest();
        Category inputCategory = TestDataFactory.createCategory(null, "Salary", Type.INCOME);
        Category savedCategory = TestDataFactory.createIncomeCategory();
        CategoryResponse response = TestDataFactory.createIncomeCategoryResponse();

        when(categoryCreateMapper.toEntity(request)).thenReturn(inputCategory);
        when(createCategoryUseCase.execute(any())).thenReturn(savedCategory);
        when(categoryResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        controller.createCategory(request);

        // Assert
        ArgumentCaptor<CategoryCreateRequest> requestCaptor =
                ArgumentCaptor.forClass(CategoryCreateRequest.class);
        verify(categoryCreateMapper, times(1)).toEntity(requestCaptor.capture());
        assertThat(requestCaptor.getValue()).isEqualTo(request);
    }

    @Test
    void createCategory_ShouldDelegateToUseCase_WhenCalled() {
        // Arrange
        CategoryCreateRequest request = TestDataFactory.createIncomeCategoryCreateRequest();
        Category inputCategory = TestDataFactory.createCategory(null, "Salary", Type.INCOME);
        Category savedCategory = TestDataFactory.createIncomeCategory();
        CategoryResponse response = TestDataFactory.createIncomeCategoryResponse();

        when(categoryCreateMapper.toEntity(request)).thenReturn(inputCategory);
        when(createCategoryUseCase.execute(inputCategory)).thenReturn(savedCategory);
        when(categoryResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        controller.createCategory(request);

        // Assert
        verify(createCategoryUseCase, times(1)).execute(inputCategory);
    }

    // updateCategory tests

    @Test
    void updateCategory_ShouldReturnUpdatedCategory_WhenCategoryExists() {
        // Arrange
        Long categoryId = 1L;
        CategoryUpdateRequest request =
                TestDataFactory.createCategoryUpdateRequest("Updated Name", Type.INCOME);
        Category existingCategory = TestDataFactory.createIncomeCategory();
        Category mergedCategory = TestDataFactory.createCategory(1L, "Updated Name", Type.INCOME);
        Category updatedCategory = TestDataFactory.createCategory(1L, "Updated Name", Type.INCOME);
        CategoryResponse response =
                TestDataFactory.createCategoryResponse(1L, "Updated Name", Type.INCOME);

        when(findCategoryByIdUseCase.execute(categoryId)).thenReturn(existingCategory);
        when(categoryUpdateRequestMapper.merge(existingCategory, request))
                .thenReturn(mergedCategory);
        when(updateCategoryUseCase.execute(mergedCategory)).thenReturn(updatedCategory);
        when(categoryResponseMapper.toDTO(updatedCategory)).thenReturn(response);

        // Act
        ResponseEntity<CategoryResponse> result = controller.updateCategory(categoryId, request);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().name()).isEqualTo("Updated Name");
    }

    @Test
    void updateCategory_ShouldReturn404_WhenCategoryDoesNotExist() {
        // Arrange
        Long categoryId = 999L;
        CategoryUpdateRequest request =
                TestDataFactory.createCategoryUpdateRequest("Updated", Type.INCOME);

        when(findCategoryByIdUseCase.execute(categoryId)).thenReturn(null);

        // Act
        ResponseEntity<CategoryResponse> result = controller.updateCategory(categoryId, request);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isNull();
        verify(findCategoryByIdUseCase, times(1)).execute(categoryId);
        verify(updateCategoryUseCase, never()).execute(any());
        verify(categoryResponseMapper, never()).toDTO(any());
    }

    @Test
    void updateCategory_ShouldMergeExistingAndRequest_WhenUpdating() {
        // Arrange
        Long categoryId = 1L;
        CategoryUpdateRequest request =
                TestDataFactory.createCategoryUpdateRequest("Updated", Type.INCOME);
        Category existingCategory = TestDataFactory.createIncomeCategory();
        Category mergedCategory = TestDataFactory.createCategory(1L, "Updated", Type.INCOME);
        Category updatedCategory = TestDataFactory.createCategory(1L, "Updated", Type.INCOME);
        CategoryResponse response =
                TestDataFactory.createCategoryResponse(1L, "Updated", Type.INCOME);

        when(findCategoryByIdUseCase.execute(categoryId)).thenReturn(existingCategory);
        when(categoryUpdateRequestMapper.merge(existingCategory, request))
                .thenReturn(mergedCategory);
        when(updateCategoryUseCase.execute(any())).thenReturn(updatedCategory);
        when(categoryResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        controller.updateCategory(categoryId, request);

        // Assert
        ArgumentCaptor<Category> existingCaptor = ArgumentCaptor.forClass(Category.class);
        ArgumentCaptor<CategoryUpdateRequest> requestCaptor =
                ArgumentCaptor.forClass(CategoryUpdateRequest.class);
        verify(categoryUpdateRequestMapper, times(1))
                .merge(existingCaptor.capture(), requestCaptor.capture());
        assertThat(existingCaptor.getValue()).isEqualTo(existingCategory);
        assertThat(requestCaptor.getValue()).isEqualTo(request);
    }

    // deleteCategory tests

    @Test
    void deleteCategory_ShouldReturn204_WhenCategoryDeleted() {
        // Arrange
        Long categoryId = 1L;
        doNothing().when(deleteCategoryUseCase).execute(categoryId);

        // Act
        ResponseEntity<Void> result = controller.deleteCategory(categoryId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
        verify(deleteCategoryUseCase, times(1)).execute(categoryId);
    }

    @Test
    void deleteCategory_ShouldDelegateToUseCase_WhenCalled() {
        // Arrange
        Long categoryId = 123L;
        doNothing().when(deleteCategoryUseCase).execute(categoryId);

        // Act
        controller.deleteCategory(categoryId);

        // Assert
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(deleteCategoryUseCase, times(1)).execute(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(123L);
    }
}
