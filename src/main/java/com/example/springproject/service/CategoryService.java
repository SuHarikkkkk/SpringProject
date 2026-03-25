package com.example.springproject.service;

import com.example.springproject.entity.Category;
import com.example.springproject.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Category category, Long id) {
        Category oldCategory = getCategoryById(id);
        if (oldCategory == null) {
            throw new RuntimeException("Категория не найдена");
        }
        oldCategory.setName(category.getName());
        oldCategory.setDescription(category.getDescription());
        return categoryRepository.save(oldCategory);
    }

    @Transactional
    public void deleteCategoryById(Long id) {
        Category category = getCategoryById(id);
        if (category == null) {
            throw new RuntimeException("Категория не найдена");
        }
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new RuntimeException("Нельзя удалить категорию с товарами");
        }
        categoryRepository.delete(category);
    }

    public List<Category> getCategoryByName(String name) {
        return categoryRepository.findAll().stream().filter(category -> category.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
    }

    public List<Category> getCategoriesWithProducts() {
        return categoryRepository.findAll().stream().filter(category -> category.getProducts() != null && !category.getProducts().isEmpty()).collect(Collectors.toList());
    }
}
