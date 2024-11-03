package com.StorageAPI.CategoryManagement.services;

import com.StorageAPI.CategoryManagement.api.CreateCategoryRequest;
import com.StorageAPI.CategoryManagement.api.UpdateCategoryRequest;
import com.StorageAPI.CategoryManagement.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class EditCategoryMapper {

    public abstract Category create(CreateCategoryRequest createCategoryRequest);

    public abstract Category update(UpdateCategoryRequest editCategoryRequest);
}
