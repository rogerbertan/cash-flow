package dev.rogerbertan.budget_planner_clean_arch.domain.usecases;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;

import java.util.List;

public interface FindAllCategoriesUseCase {

    List<Category> execute();
}
