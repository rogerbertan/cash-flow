package dev.rogerbertan.cashflow.domain.usecases.category;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
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
