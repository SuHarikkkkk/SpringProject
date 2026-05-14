package com.example.springproject.service;

import com.example.springproject.dto.CategoryCreateDto;
import com.example.springproject.dto.CategoryDto;
import com.example.springproject.entity.Category;
import com.example.springproject.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<CategoryDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(this::toDto);
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        return toDto(category);
    }

    @Transactional
    public CategoryDto saveCategory(CategoryCreateDto dto) {
        Category category = new Category();

        category.setName(dto.name());
        category.setDescription(dto.description());

        Category savedCategory = categoryRepository.save(category);

        return toDto(savedCategory);
    }

    @Transactional
    public CategoryDto updateCategory(CategoryCreateDto dto, Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        category.setName(dto.name());
        category.setDescription(dto.description());

        Category updatedCategory = categoryRepository.save(category);

        return toDto(updatedCategory);
    }

    @Transactional
    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new RuntimeException("Нельзя удалить категорию с товарами");
        }

        categoryRepository.delete(category);
    }

    private CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}