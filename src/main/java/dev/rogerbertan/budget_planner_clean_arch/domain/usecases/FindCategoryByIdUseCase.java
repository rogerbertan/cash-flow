package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;

public interface FindCategoryByIdUseCase {

    Category execute(Long id);
}
