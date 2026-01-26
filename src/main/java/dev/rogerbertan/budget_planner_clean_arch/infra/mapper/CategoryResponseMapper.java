package dev.rogerbertan.budget_planner_clean_arch.infra.mapper;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryResponseMapper {

    public CategoryResponse toDTO(Category category) {
        return new CategoryResponse(
                category.id(),
                category.name(),
                category.type()
        );
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
