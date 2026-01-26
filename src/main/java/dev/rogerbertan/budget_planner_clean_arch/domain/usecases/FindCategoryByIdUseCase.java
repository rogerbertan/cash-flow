package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;

public class FindCategoryByIdUseCase {

    private final CategoryGateway categoryGateway;

    public FindCategoryByIdUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public Category execute(Long id) {

        Category category = categoryGateway.findCategoryById(id);

        if (category == null) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }

        return category;
    }
}
