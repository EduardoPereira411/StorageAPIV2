package com.StorageAPI.ItemManagement.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFavoriteRequest {

    @NotNull
    private Boolean isFavorite;

}
