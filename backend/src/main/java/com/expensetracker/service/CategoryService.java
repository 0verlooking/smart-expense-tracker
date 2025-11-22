package com.expensetracker.service;

import com.expensetracker.dto.CategoryDTO;

import java.util.List;

/**
 * Service interface for Category operations
 */
public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO getCategoryById(Long id);

    List<CategoryDTO> getAllCategoriesByUserId(Long userId);

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);
}
