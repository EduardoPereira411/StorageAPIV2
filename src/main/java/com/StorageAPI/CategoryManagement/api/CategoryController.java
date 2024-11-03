package com.StorageAPI.CategoryManagement.api;

import com.StorageAPI.CategoryManagement.services.CategoryService;
import com.StorageAPI.ItemManagement.services.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "Categories", description = "Endpoints for managing Categories")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    private final ItemService itemService;

    private final CategoryViewMapper categoryViewMapper;

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

    @Operation(summary = "Gets all Categories")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getCategories(@RequestParam(name = "query", defaultValue = "") @Parameter(description = "Name value to search for") final String query,
                                             @RequestParam(name = "page", defaultValue = "1") @Parameter(description = "Page number of the elements") final int page,
                                             @RequestParam(required = false, defaultValue = "10") @Parameter(description = "Number of elements per page") int size,
                                             @RequestParam(name = "sortBy", defaultValue = "catPk") @Parameter(description = "Parameter to sort by") final String sortMethod,
                                             @RequestParam(name = "ascending", defaultValue = "true") @Parameter(description = "Orientation of the sorting (Asc or Desc)") final boolean sortOrientation) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("Categories", categoryViewMapper.toCategoryView(categoryService.queryByName(query,page,size, sortMethod, sortOrientation)));
        return response;
    }

    @Operation(summary = "Gets specific Category")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CategoryView> findById(@PathVariable("id") @Parameter(description = "The id of the category to find") final Long id) {
        final var category = categoryService.findById(id);
        return ResponseEntity
                .ok()
                .eTag(Long.toString(category.getVersion()))
                .body(categoryViewMapper.toCategoryView(category));
    }

    @Operation(summary = "Creates a New Category")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoryView> createCategory(@Valid @RequestBody final CreateCategoryRequest requestBody){
        final var category = categoryService.create(requestBody);
        return ResponseEntity
                .ok()
                .eTag(Long.toString(category.getVersion()))
                .body(categoryViewMapper.toCategoryView(category));
    }

    @Operation(summary = "Updates a Category")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoryView> updateCategory(HttpServletRequest request,
                                                       @PathVariable("id")@Parameter(description = "The id of the Category to update") final Long id,
                                                       @Valid @RequestBody final UpdateCategoryRequest requestBody){
        final var version = getEtag(request);
        final var category = categoryService.update(version,id, requestBody);

        return ResponseEntity
                .ok()
                .eTag(Long.toString(category.getVersion()))
                .body(categoryViewMapper.toCategoryView(category));
    }
}
