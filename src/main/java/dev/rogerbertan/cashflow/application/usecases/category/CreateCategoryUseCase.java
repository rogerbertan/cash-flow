package dev.rogerbertan.cashflow.application.usecases.category;

import dev.rogerbertan.cashflow.application.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.domain.entities.Category;

public class CreateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public CreateCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public Category execute(Category category) {

        return categoryGateway.createCategory(category);
    }
}
