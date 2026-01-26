package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;

public class CreateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public CreateCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public Category execute(Category category) {

        return categoryGateway.createCategory(category);
    }
}
