package com.StorageAPI.ItemManagement.services;

import com.StorageAPI.ItemManagement.api.CreateItemRequest;
import com.StorageAPI.ItemManagement.api.UpdateAmountRequest;
import com.StorageAPI.ItemManagement.api.EditItemRequest;
import com.StorageAPI.ItemManagement.api.UpdateFavoriteRequest;
import com.StorageAPI.ItemManagement.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ItemService {

    Item createItem(CreateItemRequest itemRequest, MultipartFile photo);

    Item findById(Long id);

    Page<Item> queryByName(String query, Boolean isFavorite, Boolean needCheckup, int page,int size, String field, boolean ascending);

    Page<Item> queryByNameOnCategory(Long categoryID, String query, Boolean isFavorite, Boolean needCheckup, int page,int size, String field, boolean ascending);

    Item updateItem(Long itemId, EditItemRequest itemRequest,MultipartFile newPhoto, Long version);

    Item updateAmount(Long itemId, UpdateAmountRequest itemAmountRequest, Long version);

    Item updateFavorite(Long itemId, UpdateFavoriteRequest updateFavoriteRequest, Long version);

    Item refreshLastModified(Long itemId);

}
