package dev.rogerbertan.cashflow.infra.mapper;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.infra.persistence.CategoryEntity;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityMapper {

    public CategoryEntity toEntity(Category category) {
        return new CategoryEntity(
                category.id(), category.name(), category.type(), LocalDateTime.now());
    }

    public Category toDomain(CategoryEntity entity) {
        return new Category(
                entity.getId(), entity.getName(), entity.getType(), entity.getCreatedAt());
    }
}
