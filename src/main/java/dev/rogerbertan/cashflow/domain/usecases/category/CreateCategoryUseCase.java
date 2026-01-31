package dev.rogerbertan.cashflow.domain.usecases.category;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;

public class CreateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public CreateCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public Category execute(Category category) {

        return categoryGateway.createCategory(category);
    }
}
