package com.StorageAPI.CategoryManagement.services;

import com.StorageAPI.CategoryManagement.api.CreateCategoryRequest;
import com.StorageAPI.CategoryManagement.api.UpdateCategoryRequest;
import com.StorageAPI.CategoryManagement.model.Category;
import org.springframework.data.domain.Page;

public interface CategoryService {

    Page<Category> findAll(int page, String field, boolean ascending);

    Category findByName(String name);

    Category findById(Long id);

    Category create(CreateCategoryRequest createCategoryRequest);

    Category update (long desiredVersion, Long id, UpdateCategoryRequest updateCategoryRequest);

    Page<Category> queryByName(String query, int page,int size, String field, boolean ascending);
}
