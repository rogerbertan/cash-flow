package dev.rogerbertan.cash_flow.infra.presentation;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.domain.usecases.*;
import dev.rogerbertan.cash_flow.infra.dto.CategoryCreateRequest;
import dev.rogerbertan.cash_flow.infra.dto.CategoryResponse;
import dev.rogerbertan.cash_flow.infra.dto.CategoryUpdateRequest;
import dev.rogerbertan.cash_flow.infra.mapper.CategoryCreateMapper;
import dev.rogerbertan.cash_flow.infra.mapper.CategoryResponseMapper;
import dev.rogerbertan.cash_flow.infra.mapper.CategoryUpdateRequestMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/categories")
public class CategoryController {

    private final FindAllCategoriesUseCase findAllCategoriesUseCase;
    private final FindCategoryByIdUseCase findCategoryByIdUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final CategoryResponseMapper categoryResponseMapper;
    private final CategoryCreateMapper categoryCreateMapper;
    private final CategoryUpdateRequestMapper categoryUpdateRequestMapper;

    public CategoryController(FindAllCategoriesUseCase findAllCategoriesUseCase, FindCategoryByIdUseCase findCategoryByIdUseCase, CreateCategoryUseCase createCategoryUseCase, UpdateCategoryUseCase updateCategoryUseCase, DeleteCategoryUseCase deleteCategoryUseCase, CategoryResponseMapper categoryResponseMapper, CategoryCreateMapper categoryCreateMapper, CategoryUpdateRequestMapper categoryUpdateRequestMapper) {
        this.findAllCategoriesUseCase = findAllCategoriesUseCase;
        this.findCategoryByIdUseCase = findCategoryByIdUseCase;
        this.createCategoryUseCase = createCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
        this.categoryResponseMapper = categoryResponseMapper;
        this.categoryCreateMapper = categoryCreateMapper;
        this.categoryUpdateRequestMapper = categoryUpdateRequestMapper;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        List<Category> categories = findAllCategoriesUseCase.execute();
        return ResponseEntity.ok(categoryResponseMapper.toListDTO(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {

        Category category = findCategoryByIdUseCase.execute(id);
        return ResponseEntity.ok(categoryResponseMapper.toDTO(category));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryCreateRequest request) {

        Category createdCategory = createCategoryUseCase.execute(categoryCreateMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseMapper.toDTO(createdCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody CategoryUpdateRequest request) {


        Category existingCategory = findCategoryByIdUseCase.execute(id);

        if (existingCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Category updatedCategory = updateCategoryUseCase.execute(categoryUpdateRequestMapper.merge(existingCategory, request));
        return ResponseEntity.ok(categoryResponseMapper.toDTO(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {

        deleteCategoryUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
