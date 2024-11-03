package com.StorageAPI.CategoryManagement.api;

import com.StorageAPI.CategoryManagement.model.Category;
import com.StorageAPI.ItemManagement.repositories.ItemRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public abstract class CategoryViewMapper {

    @Autowired
    private ItemRepository itemRepository;

    @Mapping(target = "itemCount", expression = "java(countItems(category))")
    public abstract CategoryView toCategoryView(Category category);

    public abstract Iterable<CategoryView> toCategoryView(Iterable<Category> categories);

    protected int countItems(Category category) {
        return itemRepository.countByCategory(category);
    }
}
