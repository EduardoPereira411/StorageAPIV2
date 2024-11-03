package com.StorageAPI.ShoppingList.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShoppingLineRequest {

    private int amount;

    private Boolean isBought;
}
