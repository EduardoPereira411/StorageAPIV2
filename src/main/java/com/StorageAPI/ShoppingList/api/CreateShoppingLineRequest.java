package com.StorageAPI.ShoppingList.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateShoppingLineRequest {

    @NotNull
    @NotBlank
    private String itemName;

    private int amount;
}
