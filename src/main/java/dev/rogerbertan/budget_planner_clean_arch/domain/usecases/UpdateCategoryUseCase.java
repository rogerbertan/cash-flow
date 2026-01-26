package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;

import java.time.LocalDateTime;

public class UpdateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public UpdateCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public Category execute(Category category) {
        Category existingCategory = categoryGateway.findCategoryById(category.id());

        if (existingCategory == null) {
            throw new IllegalArgumentException("Category not found with id: " + category.id());
        }

        return categoryGateway.updateCategory(
                new Category(
                    category.id(),
                    category.name(),
                    category.type(),
                LocalDateTime.now())
        );
    }
}
