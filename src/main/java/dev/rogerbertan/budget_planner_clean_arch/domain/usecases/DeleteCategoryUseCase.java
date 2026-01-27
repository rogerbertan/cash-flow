package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;
import dev.rogerbertan.budget_planner_clean_arch.infra.exception.ResourceNotFoundException;

public class DeleteCategoryUseCase{

    private final CategoryGateway categoryGateway;

    public DeleteCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public void execute(Long id) {

        if (categoryGateway.findCategoryById(id) == null) {
            throw new ResourceNotFoundException("Category", "id: " + id);
        }

        categoryGateway.deleteCategory(id);
    }
}
