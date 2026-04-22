package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.infra.dto.CategoryUpdateRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class CategoryUpdateRequestMapperTest {

    private final CategoryUpdateRequestMapper mapper = new CategoryUpdateRequestMapper();

    @Test
    void toDto_ShouldMapAllCategoryFields_WhenCalled() {
        Category category = TestDataFactory.createIncomeCategory();

        CategoryUpdateRequest result = mapper.toDto(category);

        assertThat(result.id()).isEqualTo(category.id());
        assertThat(result.name()).isEqualTo(category.name());
        assertThat(result.type()).isEqualTo(category.type());
        assertThat(result.createdAt()).isEqualTo(category.createdAt());
    }

    @Test
    void merge_ShouldPreserveExistingName_WhenDtoNameIsNull() {
        Category existing = TestDataFactory.createIncomeCategory();
        CategoryUpdateRequest dto =
                new CategoryUpdateRequest(
                        existing.id(), null, existing.type(), existing.createdAt());

        Category result = mapper.merge(existing, dto);

        assertThat(result.name()).isEqualTo(existing.name());
    }

    @Test
    void merge_ShouldUseNewName_WhenDtoNameIsNotNull() {
        Category existing = TestDataFactory.createIncomeCategory();
        CategoryUpdateRequest dto =
                new CategoryUpdateRequest(
                        existing.id(), "New Name", existing.type(), existing.createdAt());

        Category result = mapper.merge(existing, dto);

        assertThat(result.name()).isEqualTo("New Name");
    }

    @Test
    void merge_ShouldPreserveExistingType_WhenDtoTypeIsNull() {
        Category existing = TestDataFactory.createIncomeCategory();
        CategoryUpdateRequest dto =
                new CategoryUpdateRequest(
                        existing.id(), existing.name(), null, existing.createdAt());

        Category result = mapper.merge(existing, dto);

        assertThat(result.type()).isEqualTo(Type.INCOME);
    }

    @Test
    void merge_ShouldUseNewType_WhenDtoTypeIsNotNull() {
        Category existing = TestDataFactory.createIncomeCategory();
        CategoryUpdateRequest dto =
                new CategoryUpdateRequest(
                        existing.id(), existing.name(), Type.EXPENSE, existing.createdAt());

        Category result = mapper.merge(existing, dto);

        assertThat(result.type()).isEqualTo(Type.EXPENSE);
    }

    @Test
    void merge_ShouldAlwaysPreserveExistingId_WhenMerging() {
        Category existing = TestDataFactory.createIncomeCategory();
        CategoryUpdateRequest dto = mapper.toDto(existing);

        Category result = mapper.merge(existing, dto);

        assertThat(result.id()).isEqualTo(existing.id());
    }

    @Test
    void merge_ShouldAlwaysPreserveExistingCreatedAt_WhenMerging() {
        Category existing = TestDataFactory.createIncomeCategory();
        CategoryUpdateRequest dto = mapper.toDto(existing);

        Category result = mapper.merge(existing, dto);

        assertThat(result.createdAt()).isEqualTo(existing.createdAt());
    }

    @Test
    void toEntity_ShouldMapAllDtoFields_WhenCalled() {
        LocalDateTime now = LocalDateTime.now();
        CategoryUpdateRequest dto = new CategoryUpdateRequest(1L, "Salary", Type.INCOME, now);

        Category result = mapper.toEntity(dto);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Salary");
        assertThat(result.type()).isEqualTo(Type.INCOME);
        assertThat(result.createdAt()).isEqualTo(now);
    }
}
