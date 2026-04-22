package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.infra.dto.CategoryCreateRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class CategoryCreateMapperTest {

    private final CategoryCreateMapper mapper = new CategoryCreateMapper();

    @Test
    void toDTO_ShouldMapNameAndType_WhenCalled() {
        Category category = TestDataFactory.createIncomeCategory();

        CategoryCreateRequest result = mapper.toDTO(category);

        assertThat(result.name()).isEqualTo(category.name());
        assertThat(result.type()).isEqualTo(category.type());
    }

    @Test
    void toEntity_ShouldMapNameAndType_WhenCalled() {
        CategoryCreateRequest request = TestDataFactory.createIncomeCategoryCreateRequest();

        Category result = mapper.toEntity(request);

        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.type()).isEqualTo(request.type());
    }

    @Test
    void toEntity_ShouldSetIdToNull_WhenCreating() {
        CategoryCreateRequest request = TestDataFactory.createIncomeCategoryCreateRequest();

        Category result = mapper.toEntity(request);

        assertThat(result.id()).isNull();
    }

    @Test
    void toEntity_ShouldSetCreatedAtToNow_WhenCreating() {
        CategoryCreateRequest request = TestDataFactory.createIncomeCategoryCreateRequest();

        Category result = mapper.toEntity(request);

        assertThat(result.createdAt())
                .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
    }
}
