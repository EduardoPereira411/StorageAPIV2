package com.StorageAPI.ItemManagement.services;

import com.StorageAPI.CategoryManagement.model.Category;
import com.StorageAPI.CategoryManagement.repositories.CategoryRepository;
import com.StorageAPI.ItemManagement.api.CreateItemRequest;
import com.StorageAPI.ItemManagement.model.Item;
import jakarta.validation.ValidationException;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class EditItemMapper {

    @Autowired
    private CategoryRepository categoryRepository;

    public abstract Item create(CreateItemRequest createItemRequest);

    public Category toCategory(final String categoryName) {
        return categoryRepository.findByName(categoryName).orElseThrow(() -> new ValidationException("Select an existing category"));
    }
}
