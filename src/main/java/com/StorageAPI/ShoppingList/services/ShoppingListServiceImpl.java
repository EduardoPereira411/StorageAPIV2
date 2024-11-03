package com.StorageAPI.ShoppingList.services;

import com.StorageAPI.Exceptions.NotFoundException;
import com.StorageAPI.ItemManagement.model.Item;
import com.StorageAPI.ItemManagement.repositories.ItemRepository;
import com.StorageAPI.ShoppingList.api.CreateShoppingLineRequest;
import com.StorageAPI.ShoppingList.api.UpdateShoppingLineRequest;
import com.StorageAPI.ShoppingList.model.ShoppingListLine;
import com.StorageAPI.ShoppingList.repository.ShoppingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingListServiceImpl implements ShoppingListService{

    private final ShoppingListRepository shoppingListRepository;

    private final ItemRepository itemRepository;

    private ShoppingListLine findById(Long id){
        return shoppingListRepository.findById(id).orElseThrow(() -> new NotFoundException("There is no line in the shopping list with the specified Id"));
    }

    @Override
    public ShoppingListLine create(CreateShoppingLineRequest createShoppingLineRequest) {
        Item item = itemRepository.findByName(createShoppingLineRequest.getItemName()).orElseThrow(() -> new NotFoundException("There is no Item with the specified name"));
        ShoppingListLine shoppingListLine = new ShoppingListLine(item,createShoppingLineRequest.getAmount());
        return shoppingListRepository.save(shoppingListLine);
    }

    @Override
    public Page<ShoppingListLine> queryByItemName(String query, Boolean isBought, int page, int size, String field, boolean ascending) {
        Sort.Direction sortDirection = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        return shoppingListRepository.queryByName(query,isBought, PageRequest.of(page - 1, size, sortDirection, field));
    }

    @Override
    public ShoppingListLine updateShoppingListItem(Long id, UpdateShoppingLineRequest updateShoppingLineRequest, Long version) {
        ShoppingListLine shoppingListLine = findById(id);
        shoppingListLine.updateLine(version,updateShoppingLineRequest.getIsBought(),updateShoppingLineRequest.getAmount());
        return shoppingListRepository.save(shoppingListLine);
    }

    @Override
    public int deleteShoppingListLineById(Long id) {
        findById(id);
        int numberDeleted = shoppingListRepository.deleteByLineId(id);
        if(numberDeleted==0){
            throw new RuntimeException("There was an error deleting the line");
        }
        return numberDeleted;
    }

    @Override
    public int deleteWholeShoppingList() {
        int numberDeleted = shoppingListRepository.deleteAllLines();
        if(numberDeleted==0){
            throw new RuntimeException("There was an error deleting the shoppingList");
        }
        return numberDeleted;
    }


}
