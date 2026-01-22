package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;

public class CreateCategoryUseCaseImpl implements CreateCategoryUseCase{

    private final CategoryGateway categoryGateway;

    public CreateCategoryUseCaseImpl(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Override
    public Category execute(Category category) {

        return categoryGateway.createCategory(category);
    }
}
