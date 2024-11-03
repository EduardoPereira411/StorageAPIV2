package com.StorageAPI.ShoppingList.api;


import com.StorageAPI.FileManagement.FileStorageService;
import com.StorageAPI.ItemManagement.api.*;
import com.StorageAPI.ItemManagement.services.ItemService;
import com.StorageAPI.ShoppingList.services.ShoppingListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "Shopping List", description = "Endpoints for managing the Shopping List")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shoppingList")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    private final ShoppingListMapper shoppingListMapper;
    public long getEtag(HttpServletRequest request){
        final String ifMatchValue = request.getHeader("If-Match");
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must issue a conditional PATCH using 'if-match'");
        }
        if (ifMatchValue.startsWith("\"")) {
            return Long.parseLong(ifMatchValue.substring(1, ifMatchValue.length() - 1));
        }
        return Long.parseLong(ifMatchValue);

    }

    @Operation(summary = "Query threw the Shopping List by item names")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getShoppingListItems(@RequestParam(name = "query", defaultValue = "") @Parameter(description = "query string to search") final String query,
                                                    @RequestParam(name = "page", defaultValue = "1") @Parameter(description = "Page number of the elements") final int page,
                                                    @RequestParam(required = false, name = "isBought") @Parameter(description = "Whether the Value is Favorite or not") final Boolean isBought,
                                                    @RequestParam(required = false, defaultValue = "10") @Parameter(description = "Number of elements per page") int size,
                                                    @RequestParam(name = "sortBy", defaultValue = "item.category.name") @Parameter(description = "Parameter to sort by") final String sortMethod,
                                                    @RequestParam(name = "ascending", defaultValue = "true") @Parameter(description = "Orientation of the sorting (Asc or Desc)") final boolean sortOrientation) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("ShoppingListItems", shoppingListMapper.toShoppingListView(shoppingListService.queryByItemName(query,isBought,page,size,sortMethod,sortOrientation)));
        return response;
    }

    @Operation(summary = "Creates a New Shopping List Item")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ShoppingListView> createShoppingListLine(@RequestBody @Valid CreateShoppingLineRequest createShoppingLineRequest){


        final var shoppingListItem = shoppingListService.create(createShoppingLineRequest);
        return ResponseEntity
                .ok()
                .eTag(Long.toString(shoppingListItem.getVersion()))
                .body(shoppingListMapper.toShoppingListView(shoppingListItem));
    }

    @Operation(summary = "Updates an Shopping List Line details")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ShoppingListView> updateItemDetails(HttpServletRequest request,
                                                              @PathVariable("id")@Parameter(description = "The id of the Item to update") final Long id,
                                                              @RequestBody UpdateShoppingLineRequest updateShoppingLineRequest){

        final var version = getEtag(request);
        final var shoppingListLine = shoppingListService.updateShoppingListItem(id, updateShoppingLineRequest, version);

        return ResponseEntity
                .ok()
                .eTag(Long.toString(shoppingListLine.getVersion()))
                .body(shoppingListMapper.toShoppingListView(shoppingListLine));
    }

    @Operation(summary = "Deletes a line By its ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> deleteLineById(@PathVariable("id")@Parameter(description = "The id of the Item to delete") final Long id){

        Integer deletedLines = shoppingListService.deleteShoppingListLineById(id);

        return ResponseEntity
                .ok()
                .body(deletedLines);
    }

    @Operation(summary = "Deletes a whole shopping cart")
    @DeleteMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> deleteAll(){

        Integer deletedLines = shoppingListService.deleteWholeShoppingList();

        return ResponseEntity
                .ok()
                .body(deletedLines);
    }
}
