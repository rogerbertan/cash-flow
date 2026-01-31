package dev.rogerbertan.cashflow.infra.gateway;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.infra.mapper.CategoryEntityMapper;
import dev.rogerbertan.cashflow.infra.persistence.CategoryEntity;
import dev.rogerbertan.cashflow.infra.persistence.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CategoryRepositoryGateway implements CategoryGateway {

    private final CategoryRepository categoryRepository;
    private final CategoryEntityMapper entityMapper;

    public CategoryRepositoryGateway(
            CategoryRepository categoryRepository, CategoryEntityMapper entityMapper) {
        this.categoryRepository = categoryRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public List<Category> findAllCategories() {

        return categoryRepository.findAll().stream().map(entityMapper::toDomain).toList();
    }

    @Override
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id).map(entityMapper::toDomain).orElse(null);
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {

        CategoryEntity savedEntity = categoryRepository.save(entityMapper.toEntity(category));

        return entityMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public Category updateCategory(Category category) {

        CategoryEntity updatedEntity = categoryRepository.save(entityMapper.toEntity(category));

        return entityMapper.toDomain(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {

        categoryRepository.deleteById(id);
    }
}
