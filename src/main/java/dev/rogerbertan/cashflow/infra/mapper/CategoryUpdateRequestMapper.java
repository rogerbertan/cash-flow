package dev.rogerbertan.cashflow.infra.mapper;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.infra.dto.CategoryUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class CategoryUpdateRequestMapper {

    public CategoryUpdateRequest toDto(Category category) {
        return new CategoryUpdateRequest(
                category.id(), category.name(), category.type(), category.createdAt());
    }

    public Category merge(Category existentCategory, CategoryUpdateRequest dto) {
        return new Category(
                existentCategory.id(),
                dto.name() != null ? dto.name() : existentCategory.name(),
                dto.type() != null ? dto.type() : existentCategory.type(),
                existentCategory.createdAt());
    }

    public Category toEntity(CategoryUpdateRequest dto) {
        return new Category(dto.id(), dto.name(), dto.type(), dto.createdAt());
    }
}
