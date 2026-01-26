package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;

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
