package com.StorageAPI.ItemManagement.api;

import com.StorageAPI.ItemManagement.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ItemViewMapper {

    @Mapping(source = "category.name", target = "categoryName")
    public abstract ItemDetailedView toItemDetailedView(Item item);

    @Mapping(source = "category.name", target = "categoryName")
    public abstract Iterable<ItemDetailedView> toItemDetailedView(Iterable<Item> items);

    @Mapping(source = "category.name", target = "categoryName")
    public abstract ItemView toItemView(Item item);

    @Mapping(source = "category.name", target = "categoryName")
    public abstract Iterable<ItemView> toItemView(Iterable<Item> items);

}
