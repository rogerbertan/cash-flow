package dev.rogerbertan.budget_planner_clean_arch.infra.gateway;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.CategoryEntityMapper;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.CategoryEntity;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryRepositoryGateway implements CategoryGateway {

    private final CategoryRepository categoryRepository;
    private final CategoryEntityMapper entityMapper;

    public CategoryRepositoryGateway(CategoryRepository categoryRepository, CategoryEntityMapper entityMapper) {
        this.categoryRepository = categoryRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public List<Category> findAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Category createCategory(Category category) {

        CategoryEntity savedEntity = categoryRepository.save(entityMapper.toEntity(category));

        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Category updateCategory(Category category) {

        CategoryEntity updatedEntity = categoryRepository.save(entityMapper.toEntity(category));

        return entityMapper.toDomain(updatedEntity);
    }

    @Override
    public void deleteCategory(Long id) {

        categoryRepository.deleteById(id);
    }
}
