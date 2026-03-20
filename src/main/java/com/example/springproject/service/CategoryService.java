package com.example.springproject.service;

import com.example.springproject.entity.Category;
import com.example.springproject.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public void deleteCategoryById(Long id) {
        Category category = findCategoryById(id);
        categoryRepository.delete(category);
    }

}
