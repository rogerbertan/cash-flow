package dev.rogerbertan.cashflow.infra.mapper;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.infra.dto.CategoryCreateRequest;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class CategoryCreateMapper {

    public CategoryCreateRequest toDTO(Category category) {
        return new CategoryCreateRequest(category.name(), category.type());
    }

    public Category toEntity(CategoryCreateRequest dto) {
        return new Category(null, dto.name(), dto.type(), LocalDateTime.now());
    }
}
