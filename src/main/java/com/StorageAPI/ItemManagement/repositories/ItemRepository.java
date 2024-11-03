package com.StorageAPI.ItemManagement.repositories;

import com.StorageAPI.CategoryManagement.model.Category;
import com.StorageAPI.ItemManagement.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Override
    Optional<Item> findById(Long id);

    @Query("SELECT i FROM Item i WHERE i.name = :name")
    Optional<Item>findByName(String name);

    @Query("SELECT COUNT(i) FROM Item i WHERE i.category = :category")
    int countByCategory(Category category);

    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(concat('%',:name, '%')) AND (:isFavorite IS NULL OR i.isFavorite = :isFavorite) AND (:needsCheckup IS NULL OR i.needsCheckup = :needsCheckup)")
    Page<Item>QueryByName(String name,Boolean isFavorite, Boolean needsCheckup, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.category.catPk = :categoryID AND LOWER(i.name) LIKE LOWER(concat('%',:name, '%')) AND (:isFavorite IS NULL OR i.isFavorite = :isFavorite) AND (:needsCheckup IS NULL OR i.needsCheckup = :needsCheckup)")
    Page<Item> QueryItemsOnCategory(Long categoryID, String name, Boolean isFavorite, Boolean needsCheckup, Pageable pageable);
}
