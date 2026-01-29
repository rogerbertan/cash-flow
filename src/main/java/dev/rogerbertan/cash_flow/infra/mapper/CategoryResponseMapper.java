package dev.rogerbertan.cash_flow.infra.mapper;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.infra.dto.CategoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryResponseMapper {

    public CategoryResponse toDTO(Category category) {
        return new CategoryResponse(
                category.id(),
                category.name(),
                category.type()
        );
    }

    public List<CategoryResponse> toListDTO(List<Category> categories) {
        return categories.stream()
                .map(this::toDTO)
                .toList();
    }

    public Category toEntity(CategoryResponse dto) {
        return new Category(
                dto.id(),
                dto.name(),
                dto.type(),
                null
        );
    }
}
