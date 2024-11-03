package com.StorageAPI.ItemManagement.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemRequest {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String category;

    private String description;

    private Integer updateFrequency;

}
