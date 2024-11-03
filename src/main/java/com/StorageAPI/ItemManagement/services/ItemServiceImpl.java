package com.StorageAPI.ItemManagement.services;

import com.StorageAPI.CategoryManagement.model.Category;
import com.StorageAPI.CategoryManagement.repositories.CategoryRepository;
import com.StorageAPI.Exceptions.NotFoundException;
import com.StorageAPI.FileManagement.FileStorageService;
import com.StorageAPI.ItemManagement.api.CreateItemRequest;
import com.StorageAPI.ItemManagement.api.UpdateAmountRequest;
import com.StorageAPI.ItemManagement.api.EditItemRequest;
import com.StorageAPI.ItemManagement.api.UpdateFavoriteRequest;
import com.StorageAPI.ItemManagement.model.Item;
import com.StorageAPI.ItemManagement.repositories.ItemRepository;
import com.StorageAPI.Utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{


    private final ItemRepository itemRepository;

    private final CategoryRepository categoryRepository;

    private final EditItemMapper editItemMapper;

    private final FileStorageService fileService;

    private final Utils utils;

    private Category findCategoryByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(() -> new NotFoundException("There is no Category with the specified name"));
    }

    @Override
    public Item createItem(CreateItemRequest itemRequest, MultipartFile photo) {
        final Item item = editItemMapper.create(itemRequest);

        Item savedItem = itemRepository.save(item);
        if (!(photo == null || photo.isEmpty())) {
            final var fileResponse = utils.uploadFile("/item/photo/", String.valueOf(savedItem.getItemPk()), photo);
            savedItem.setImage(fileResponse.getFileDownloadUri());
        }
        return savedItem;
    }

    @Override
    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("There is no Item with the specified Id"));
    }

    @Override
    public Page<Item> queryByName(String query, Boolean isFavorite, Boolean needCheckup, int page, int size, String field, boolean ascending) {
        Sort.Direction sortDirection = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        return itemRepository.QueryByName(query, isFavorite, needCheckup, PageRequest.of(page - 1, size, sortDirection, field));
    }

    @Override
    public Page<Item> queryByNameOnCategory(Long categoryID, String query, Boolean isFavorite, Boolean needCheckup, int page, int size, String field, boolean ascending) {
        Sort.Direction sortDirection = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        return itemRepository.QueryItemsOnCategory(categoryID,query, isFavorite, needCheckup, PageRequest.of(page - 1, size, sortDirection, field));
    }

    @Override
    public Item updateItem(Long itemId, EditItemRequest itemRequest, MultipartFile newPhoto, Long version) {
        Item item = findById(itemId);
        String oldImageURI = item.getImage();
        String newImageURI;

        if (newPhoto != null && !newPhoto.isEmpty()) {
            var fileResponse = utils.uploadFile("/item/photo/", String.valueOf(item.getItemPk()), newPhoto);
            newImageURI = fileResponse.getFileDownloadUri();

            if (oldImageURI != null) {
                utils.deleteFile(oldImageURI);
            }
        } else {
            newImageURI = oldImageURI;
        }

        Category category = findCategoryByName(itemRequest.getCategory());
        item.updateItem(version, itemRequest.getName(), itemRequest.getDescription(), category, itemRequest.getFavorite(), itemRequest.getUpdateFrequency(), newImageURI);

        return itemRepository.save(item);
    }

    @Override
    public Item updateAmount(Long itemId, UpdateAmountRequest itemAmountRequest, Long version) {
        Item item = findById(itemId);
        item.updateAmount(version,itemAmountRequest.getAmount());
        return itemRepository.save(item);
    }

    @Override
    public Item updateFavorite(Long itemId, UpdateFavoriteRequest updateFavoriteRequest, Long version) {
        Item item = findById(itemId);
        item.updateIsFavorite(version,updateFavoriteRequest.getIsFavorite());
        return itemRepository.save(item);
    }

    @Override
    public Item refreshLastModified(Long itemId){
        Item item = findById(itemId);
        item.refreshLastModified();
        return itemRepository.save(item);
    }


}
