package com.StorageAPI.ShoppingList.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Item information to be displayed in the cart")
public class ItemView {

    private Long itemPk;

    private String name;

    private String categoryName;

    private int amount;

    private boolean needsCheckup;

    private boolean isFavorite;

    private LocalDate lastModifiedAt;

    private String image;

}