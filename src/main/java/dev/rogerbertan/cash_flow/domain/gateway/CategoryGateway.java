package dev.rogerbertan.cash_flow.domain.gateway;

import dev.rogerbertan.cash_flow.domain.entities.Category;

import java.util.List;

public interface CategoryGateway {

    List<Category> findAllCategories();
    Category findCategoryById(Long id);
    Category createCategory(Category category);
    Category updateCategory(Category category);
    void deleteCategory(Long id);
}
