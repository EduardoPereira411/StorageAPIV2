package com.StorageAPI.ShoppingList.repository;

import com.StorageAPI.ShoppingList.model.ShoppingListLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ShoppingListRepository extends JpaRepository<ShoppingListLine, Long> {

    @Query("SELECT i FROM ShoppingListLine i WHERE LOWER(i.item.name) LIKE LOWER(concat('%',:itemName, '%')) AND (:isBought IS NULL OR i.isBought = :isBought)")
    Page<ShoppingListLine> queryByName(String itemName, Boolean isBought, Pageable pageable);

    @Modifying
    @Query("DELETE FROM ShoppingListLine d WHERE d.linePk = :lineID")
    int deleteByLineId(Long lineID);

    @Modifying
    @Query("DELETE FROM ShoppingListLine")
    int deleteAllLines();
}
