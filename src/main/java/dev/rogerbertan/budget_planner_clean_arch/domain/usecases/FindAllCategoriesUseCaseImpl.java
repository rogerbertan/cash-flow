package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;

import java.util.List;

public class FindAllCategoriesUseCaseImpl implements FindAllCategoriesUseCase{

    private final CategoryGateway categoryGateway;

    public FindAllCategoriesUseCaseImpl(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Override
    public List<Category> execute() {

        List<Category> categories = categoryGateway.findAllCategories();
        return categories;
    }
}
