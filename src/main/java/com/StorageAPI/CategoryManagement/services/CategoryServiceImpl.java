package com.StorageAPI.CategoryManagement.services;

import com.StorageAPI.CategoryManagement.api.CreateCategoryRequest;
import com.StorageAPI.CategoryManagement.api.UpdateCategoryRequest;
import com.StorageAPI.CategoryManagement.model.Category;
import com.StorageAPI.CategoryManagement.repositories.CategoryRepository;
import com.StorageAPI.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    private final EditCategoryMapper editCategoryMapper;
    public Page<Category> findAll(int page, String field, boolean ascending){
        if(ascending) {
            return categoryRepository.findAll(PageRequest.of(page - 1, 10).withSort(Sort.by(Sort.Direction.ASC, field)));
        }else{
            return categoryRepository.findAll(PageRequest.of(page - 1, 10).withSort(Sort.by(Sort.Direction.DESC, field)));
        }
    }

    public Page<Category> queryByName(String query, int page, int size, String field, boolean ascending){
        Sort.Direction sortDirection = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        return categoryRepository.findByNameContainingIgnoreCase(query, PageRequest.of(page - 1, size, sortDirection, field));
    }

    public Category findByName(String name){
        Optional<Category> cat = categoryRepository.findByName(name);
        if(cat.isPresent()) {
            return cat.get();
        }else {
            throw new NotFoundException("There are no Categories with this name");
        }
    }

    public Category findById(Long id){
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("There is no Category with this id"));
    }

    public Category create(CreateCategoryRequest createCategoryRequest){
        final Category cat = editCategoryMapper.create(createCategoryRequest);
        return categoryRepository.save(cat);
    }

    public Category update (long desiredVersion, Long id, UpdateCategoryRequest updateCategoryRequest){
        Category cat = findById(id);
        cat.update(desiredVersion ,updateCategoryRequest.getName(),updateCategoryRequest.getDescription());
        return categoryRepository.save(cat);

    }
}
