package dev.rogerbertan.cash_flow.domain.usecases;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.domain.gateway.CategoryGateway;

import java.util.List;

public class FindAllCategoriesUseCase{

    private final CategoryGateway categoryGateway;

    public FindAllCategoriesUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public List<Category> execute() {

        List<Category> categories = categoryGateway.findAllCategories();
        return categories;
    }
}
