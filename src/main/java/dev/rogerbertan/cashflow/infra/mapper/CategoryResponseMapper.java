package dev.rogerbertan.cashflow.infra.mapper;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.infra.dto.CategoryResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CategoryResponseMapper {

    public CategoryResponse toDTO(Category category) {
        return new CategoryResponse(category.id(), category.name(), category.type());
    }

    public List<CategoryResponse> toListDTO(List<Category> categories) {
        return categories.stream().map(this::toDTO).toList();
    }

    public Category toEntity(CategoryResponse dto) {
        return new Category(dto.id(), dto.name(), dto.type(), null);
    }
}
