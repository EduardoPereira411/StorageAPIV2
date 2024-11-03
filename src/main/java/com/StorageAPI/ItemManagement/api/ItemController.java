package com.StorageAPI.ItemManagement.api;


import com.StorageAPI.FileManagement.FileStorageService;
import com.StorageAPI.ItemManagement.services.ItemService;
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

@Tag(name = "Items", description = "Endpoints for managing Items")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/item")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final FileStorageService fileStorageService;

    private final ItemService itemService;

    private final ItemViewMapper itemViewMapper;
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

    @Operation(summary = "Query threw all Items")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getItems(@RequestParam(name = "query", defaultValue = "") @Parameter(description = "query string to search") final String query,
                                        @RequestParam(name = "page", defaultValue = "1") @Parameter(description = "Page number of the elements") final int page,
                                        @RequestParam(required = false, name = "isFavorite") @Parameter(description = "Whether the Value is Favorite or not") final Boolean isFavorite,
                                        @RequestParam(required = false, name = "needsCheckup") @Parameter(description = "Whether the Items needs a checkup or not") final Boolean needsCheckup,
                                        @RequestParam(required = false, defaultValue = "10") @Parameter(description = "Number of elements per page") int size,
                                        @RequestParam(name = "sortBy", defaultValue = "itemPk") @Parameter(description = "Parameter to sort by") final String sortMethod,
                                        @RequestParam(name = "ascending", defaultValue = "true") @Parameter(description = "Orientation of the sorting (Asc or Desc)") final boolean sortOrientation) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("Items", itemViewMapper.toItemView(itemService.queryByName(query,isFavorite,needsCheckup,page,size,sortMethod,sortOrientation)));
        return response;
    }

    @Operation(summary = "Query threw all the items inside a category")
    @GetMapping("/category/{categoryID}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getItemsOnCategory(@PathVariable Long categoryID,
                                                  @RequestParam(name = "query", defaultValue = "") @Parameter(description = "query string to search") final String query,
                                                  @RequestParam(name = "page", defaultValue = "1") @Parameter(description = "Page number of the elements") final int page,
                                                  @RequestParam(required = false, name = "isFavorite") @Parameter(description = "Whether the Value is Favorite or not") final Boolean isFavorite,
                                                  @RequestParam(required = false, name = "needsCheckup") @Parameter(description = "Whether the Items needs a checkup or not") final Boolean needsCheckup,
                                                  @RequestParam(required = false, defaultValue = "10") @Parameter(description = "Number of elements per page") int size,
                                                  @RequestParam(name = "sortBy", defaultValue = "itemPk") @Parameter(description = "Parameter to sort by") final String sortMethod,
                                                  @RequestParam(name = "ascending", defaultValue = "true") @Parameter(description = "Orientation of the sorting (Asc or Desc)") final boolean sortOrientation) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("Items", itemViewMapper.toItemView(itemService.queryByNameOnCategory(categoryID,query,isFavorite,needsCheckup,page,size,sortMethod,sortOrientation)));
        return response;
    }

    @Operation(summary = "Gets a specific Item")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ItemDetailedView> findById(@PathVariable("id") @Parameter(description = "The id of the item to find") final Long id) {
        final var item = itemService.findById(id);
        return ResponseEntity
                .ok()
                .eTag(Long.toString(item.getVersion()))
                .body(itemViewMapper.toItemDetailedView(item));
    }

    @Operation(summary = "Creates a New Item")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ItemDetailedView> createItem(@RequestPart(name="json") String jsonString,
                                                       @RequestPart(name="photo", required=false) MultipartFile file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        CreateItemRequest createItemRequest = objectMapper.readValue(jsonString, CreateItemRequest.class);

        final var item = itemService.createItem(createItemRequest, file);
        return ResponseEntity
                .ok()
                .eTag(Long.toString(item.getVersion()))
                .body(itemViewMapper.toItemDetailedView(item));
    }

    @Operation(summary = "Downloads the photo of the item")
    @GetMapping("/{itemId}/photo/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable final String fileName,
                                                 final HttpServletRequest request) {
        // Load file as Resource
        final Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (final IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Operation(summary = "Updates an Items details")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ItemDetailedView> updateItemDetails(HttpServletRequest request,
                                                              @PathVariable("id")@Parameter(description = "The id of the Item to update") final Long id,
                                                              @RequestPart(name="json") String jsonString,
                                                              @RequestPart(name="photo", required=false) MultipartFile file) throws IOException{

        ObjectMapper objectMapper = new ObjectMapper();
        EditItemRequest editItemRequest = objectMapper.readValue(jsonString, EditItemRequest.class);

        final var version = getEtag(request);
        final var item = itemService.updateItem(id, editItemRequest, file, version);

        return ResponseEntity
                .ok()
                .eTag(Long.toString(item.getVersion()))
                .body(itemViewMapper.toItemDetailedView(item));
    }

    @Operation(summary = "Updates an Items Amount")
    @PatchMapping("/amount/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ItemDetailedView> updateItemAmount(HttpServletRequest request,
                                                             @PathVariable Long itemId,
                                                             @Valid @RequestBody final UpdateAmountRequest requestBody){
        final var version = getEtag(request);
        final var item = itemService.updateAmount(itemId,requestBody,version);

        return ResponseEntity
                .ok()
                .eTag(Long.toString(item.getVersion()))
                .body(itemViewMapper.toItemDetailedView(item));
    }

    @Operation(summary = "Toggles if an item is favorite or no")
    @PatchMapping("/favorite/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ItemDetailedView> updateItemFavorite(HttpServletRequest request,
                                                               @PathVariable Long itemId,
                                                               @Valid @RequestBody final UpdateFavoriteRequest requestBody){

        final var version = getEtag(request);
        final var item = itemService.updateFavorite(itemId,requestBody,version);

        return ResponseEntity
                .ok()
                .eTag(Long.toString(item.getVersion()))
                .body(itemViewMapper.toItemDetailedView(item));
    }

    @Operation(summary = "Refreshes the 'LastModifiedDate' to the day of the request, in case the amount hasn't changed in a while")
    @PatchMapping("/refreshCheckupDate/{itemId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ItemDetailedView> refreshCheckupDate(@PathVariable Long itemId){

        final var item = itemService.refreshLastModified(itemId);

        return ResponseEntity
                .ok()
                .eTag(Long.toString(item.getVersion()))
                .body(itemViewMapper.toItemDetailedView(item));
    }


}
