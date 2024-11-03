package com.StorageAPI.ItemManagement.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "A normal ItemView containing basic information")
public class ItemView {

    private Long itemPk;

    private long version;

    private String name;

    private int amount;

    private boolean isFavorite;

    private boolean needsCheckup;

    private String categoryName;

    private LocalDate lastModifiedAt;

    private String image;
}