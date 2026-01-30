package dev.rogerbertan.cash_flow.domain.usecases.category;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cash_flow.infra.exception.ResourceNotFoundException;

import java.time.LocalDateTime;

public class UpdateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public UpdateCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public Category execute(Category category) {
        Category existingCategory = categoryGateway.findCategoryById(category.id());

        if (existingCategory == null) {
            throw new ResourceNotFoundException("Category", "id: " + category.id());
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
