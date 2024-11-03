package com.StorageAPI.ShoppingList.api;

import com.StorageAPI.ItemManagement.model.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Schema(description = "A View containing the information about a item on the shopping list")
public class ShoppingListView {

    private Long linePk;

    private long version;

    private ItemView item;

    private int amount;

    private boolean isBought;
}
