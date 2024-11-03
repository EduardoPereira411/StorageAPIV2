package com.StorageAPI.ItemManagement.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "An Item View for All its details")
public class ItemDetailedView {

    private Long itemPk;

    private String name;

    private int amount;

    private boolean isFavorite;

    private int updateFrequency;

    private boolean needsCheckup;

    private String description;

    private String categoryName;

    private LocalDate lastModifiedAt;

    private String image;
}