package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.infra.dto.CategoryResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class CategoryResponseMapperTest {

    private final CategoryResponseMapper mapper = new CategoryResponseMapper();

    @Test
    void toDTO_ShouldMapAllCategoryFieldsToResponse_WhenCalled() {
        Category category = TestDataFactory.createIncomeCategory();

        CategoryResponse result = mapper.toDTO(category);

        assertThat(result.id()).isEqualTo(category.id());
        assertThat(result.name()).isEqualTo(category.name());
        assertThat(result.type()).isEqualTo(category.type());
    }

    @Test
    void toListDTO_ShouldMapAllCategories_WhenListIsProvided() {
        Category income = TestDataFactory.createIncomeCategory();
        Category expense = TestDataFactory.createExpenseCategory();

        List<CategoryResponse> result = mapper.toListDTO(List.of(income, expense));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(income.id());
        assertThat(result.get(1).id()).isEqualTo(expense.id());
    }

    @Test
    void toListDTO_ShouldReturnEmptyList_WhenInputListIsEmpty() {
        List<CategoryResponse> result = mapper.toListDTO(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void toEntity_ShouldMapAllResponseFieldsToCategory_WhenCalled() {
        CategoryResponse response = TestDataFactory.createIncomeCategoryResponse();

        Category result = mapper.toEntity(response);

        assertThat(result.id()).isEqualTo(response.id());
        assertThat(result.name()).isEqualTo(response.name());
        assertThat(result.type()).isEqualTo(response.type());
    }

    @Test
    void toEntity_ShouldSetCreatedAtToNull_WhenMappingFromResponse() {
        CategoryResponse response = TestDataFactory.createIncomeCategoryResponse();

        Category result = mapper.toEntity(response);

        assertThat(result.createdAt()).isNull();
    }
}
