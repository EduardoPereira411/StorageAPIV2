package com.StorageAPI.ItemManagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditItemRequest {

    private String name;

    private String description;

    private String category;

    private Boolean favorite;

    private Integer updateFrequency;

}