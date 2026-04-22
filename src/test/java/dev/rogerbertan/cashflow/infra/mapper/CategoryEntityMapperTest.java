package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.infra.persistence.CategoryEntity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class CategoryEntityMapperTest {

    private final CategoryEntityMapper mapper = new CategoryEntityMapper();

    @Test
    void toEntity_ShouldMapAllCategoryFieldsToEntity_WhenCalled() {
        Category category = TestDataFactory.createIncomeCategory();

        CategoryEntity result = mapper.toEntity(category);

        assertThat(result.getId()).isEqualTo(category.id());
        assertThat(result.getName()).isEqualTo(category.name());
        assertThat(result.getType()).isEqualTo(category.type());
    }

    @Test
    void toEntity_ShouldSetCreatedAtToNow_WhenMapping() {
        Category category = TestDataFactory.createIncomeCategory();

        CategoryEntity result = mapper.toEntity(category);

        assertThat(result.getCreatedAt())
                .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void toDomain_ShouldMapAllEntityFieldsToDomain_WhenCalled() {
        CategoryEntity entity = TestDataFactory.createIncomeCategoryEntity();

        Category result = mapper.toDomain(entity);

        assertThat(result.id()).isEqualTo(entity.getId());
        assertThat(result.name()).isEqualTo(entity.getName());
        assertThat(result.type()).isEqualTo(entity.getType());
        assertThat(result.createdAt()).isEqualTo(entity.getCreatedAt());
    }
}
