package dev.rogerbertan.cashflow.domain.usecases.category;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.infra.exception.ResourceNotFoundException;
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
                new Category(category.id(), category.name(), category.type(), LocalDateTime.now()));
    }
}
