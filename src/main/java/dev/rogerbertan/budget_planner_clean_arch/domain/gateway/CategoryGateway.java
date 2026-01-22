package dev.rogerbertan.budget_planner_clean_arch.domain.gateway;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;

import java.util.List;

public interface CategoryGateway {

    List<Category> findAllCategories();
    Category createCategory(Category category);
    Category updateCategory(Category category);
    void deleteCategory(Long id);
}
