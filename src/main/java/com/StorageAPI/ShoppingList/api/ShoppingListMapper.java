package com.StorageAPI.ShoppingList.api;

import com.StorageAPI.ItemManagement.api.ItemDetailedView;
import com.StorageAPI.ItemManagement.model.Item;
import com.StorageAPI.ShoppingList.model.ShoppingListLine;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class ShoppingListMapper {

    public abstract ShoppingListView toShoppingListView(ShoppingListLine shoppingListLine);

    public abstract Iterable<ShoppingListView> toShoppingListView(Iterable<ShoppingListLine> ShoppingListLines);

    @Mapping(source = "category.name", target = "categoryName")
    public abstract ItemView toItemView(Item item);

    @AfterMapping
    protected void mapItemToItemView(ShoppingListLine shoppingListLine, @MappingTarget ShoppingListView shoppingListView) {
        shoppingListView.setItem(toItemView(shoppingListLine.getItem()));
    }
}
