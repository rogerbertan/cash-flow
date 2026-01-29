package dev.rogerbertan.cash_flow.infra.mapper;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.infra.dto.CategoryCreateRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoryCreateMapper {

    public CategoryCreateRequest toDTO(Category category) {
        return new CategoryCreateRequest(
                category.name(),
                category.type()
        );
    }

    public Category toEntity(CategoryCreateRequest dto) {
        return new Category(
                null,
                dto.name(),
                dto.type(),
                LocalDateTime.now()
        );
    }
}
