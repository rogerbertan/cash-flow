package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;

public interface UpdateCategoryUseCase {

    Category execute(Category category);
}
