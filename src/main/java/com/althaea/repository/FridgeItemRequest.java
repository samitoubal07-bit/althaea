package com.althaea.dto;

import com.althaea.model.FridgeItem;
import jakarta.validation.constraints.*;
public record FridgeItemRequest(
        @NotBlank String name,
        double quantity,
        String unit,
        double calsPer100g,
        double proteinPer100g,
        double carbsPer100g,
        double fatPer100g,
        @NotNull FridgeItem.FoodCategory category
) {}
