package dev.rogerbertan.cashflow.domain.usecases.category;

import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;

public class DeleteCategoryUseCase {

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
