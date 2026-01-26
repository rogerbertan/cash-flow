package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;

public class DeleteCategoryUseCase{

    private final CategoryGateway categoryGateway;

    public DeleteCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public void execute(Long id) {

        if (categoryGateway.findCategoryById(id) == null) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }

        categoryGateway.deleteCategory(id);
    }
}
