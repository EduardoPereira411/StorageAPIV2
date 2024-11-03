package com.StorageAPI.ShoppingList.services;

import com.StorageAPI.ShoppingList.api.CreateShoppingLineRequest;
import com.StorageAPI.ShoppingList.api.UpdateShoppingLineRequest;
import com.StorageAPI.ShoppingList.model.ShoppingListLine;
import org.springframework.data.domain.Page;

public interface ShoppingListService {

    ShoppingListLine create(CreateShoppingLineRequest createShoppingLineRequest);

    Page<ShoppingListLine> queryByItemName(String query, Boolean isBought,  int page,int size, String field, boolean ascending);

    ShoppingListLine updateShoppingListItem(Long id, UpdateShoppingLineRequest updateShoppingLineRequest, Long version);

    int deleteShoppingListLineById(Long id);

    int deleteWholeShoppingList();
}
