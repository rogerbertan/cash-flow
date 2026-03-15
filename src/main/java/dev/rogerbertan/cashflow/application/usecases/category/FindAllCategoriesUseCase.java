package dev.rogerbertan.cashflow.application.usecases.category;

import dev.rogerbertan.cashflow.application.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.domain.entities.Category;
import java.util.List;

public class FindAllCategoriesUseCase {

    private final CategoryGateway categoryGateway;

    public FindAllCategoriesUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public List<Category> execute() {

        List<Category> categories = categoryGateway.findAllCategories();
        return categories;
    }
}
