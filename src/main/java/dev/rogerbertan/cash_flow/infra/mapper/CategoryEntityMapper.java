package dev.rogerbertan.cash_flow.infra.mapper;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.infra.persistence.CategoryEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoryEntityMapper {

    public CategoryEntity toEntity(Category category) {
        return new CategoryEntity(
                category.id(),
                category.name(),
                category.type(),
                LocalDateTime.now()
        );
    }

    public Category toDomain(CategoryEntity entity) {
        return new Category(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getCreatedAt()
        );
    }
}
