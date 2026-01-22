package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;

import java.time.LocalDateTime;

public class UpdateCategoryUseCaseImpl implements UpdateCategoryUseCase{

    private final CategoryGateway categoryGateway;

    public UpdateCategoryUseCaseImpl(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Override
    public Category execute(Category category) {
        Category existingCategory = categoryGateway.findCategoryById(category.id());

        if (existingCategory == null) {
            throw new IllegalArgumentException("Category not found with id: " + category.id());
        }

        return categoryGateway.updateCategory(
                new Category(
                    existingCategory.id(),
                    existingCategory.name(),
                    existingCategory.type(),
                LocalDateTime.now())
        );
    }
}
