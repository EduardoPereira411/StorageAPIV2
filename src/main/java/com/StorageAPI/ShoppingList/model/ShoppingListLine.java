package com.StorageAPI.ShoppingList.model;

import com.StorageAPI.Exceptions.ConflictException;
import com.StorageAPI.ItemManagement.model.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.DataIntegrityViolationException;

@Entity
public class ShoppingListLine {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long linePk;

    @Version
    @Getter
    private long version;

    @Getter
    @Setter
    @OneToOne
    private Item item;

    @Getter
    @Setter
    private int amount;

    @Getter
    @Setter
    private boolean isBought = false;

    public ShoppingListLine() {
    }

    public ShoppingListLine(Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public void updateLine(final long desiredVersion, final Boolean isBought, final Integer amount) {
        if (this.version != desiredVersion) {
            throw new ConflictException("Object was modified by another user");
        }
        if(isBought!=null) {
            setBought(isBought);
        }
        if(amount!=null) {
            setAmount(amount);
        }
    }
}
