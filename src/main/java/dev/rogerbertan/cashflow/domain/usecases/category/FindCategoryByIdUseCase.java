package dev.rogerbertan.cashflow.domain.usecases.category;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;

public class FindCategoryByIdUseCase {

    private final CategoryGateway categoryGateway;

    public FindCategoryByIdUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public Category execute(Long id) {

        Category category = categoryGateway.findCategoryById(id);

        if (category == null) {
            throw new ResourceNotFoundException("Category", "id: " + id);
        }

        return category;
    }
}
