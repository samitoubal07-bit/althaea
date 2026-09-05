package com.althaea.service;

import com.althaea.model.FridgeItem;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/** Estimates nutritional values for common ingredients.*/

@Service
public class FoodLookupService {

    private record NutritionData(double cals, double protein, double carbs, double fat, FridgeItem.FoodCategory category) {}

    private static final Map<String, NutritionData> FOOD_DB = new HashMap<>();

    static {
        // Proteins
        FOOD_DB.put("chicken breast",   new NutritionData(165, 31, 0,   3.6, FridgeItem.FoodCategory.PROTEIN));
        FOOD_DB.put("chicken thigh",    new NutritionData(209, 26, 0,   11,  FridgeItem.FoodCategory.PROTEIN));
        FOOD_DB.put("beef mince",       new NutritionData(254, 26, 0,   17,  FridgeItem.FoodCategory.PROTEIN));
        FOOD_DB.put("salmon",           new NutritionData(208, 20, 0,   13,  FridgeItem.FoodCategory.PROTEIN));
        FOOD_DB.put("tuna",             new NutritionData(116, 26, 0,   1,   FridgeItem.FoodCategory.PROTEIN));
        FOOD_DB.put("eggs",             new NutritionData(155, 13, 1.1, 11,  FridgeItem.FoodCategory.PROTEIN));
        FOOD_DB.put("turkey",           new NutritionData(189, 29, 0,   7,   FridgeItem.FoodCategory.PROTEIN));
        FOOD_DB.put("pork",             new NutritionData(242, 27, 0,   14,  FridgeItem.FoodCategory.PROTEIN));
        FOOD_DB.put("tofu",             new NutritionData(76,  8,  2,   4,   FridgeItem.FoodCategory.PROTEIN));
        FOOD_DB.put("greek yogurt",     new NutritionData(59,  10, 4,   0.4, FridgeItem.FoodCategory.PROTEIN));
        FOOD_DB.put("cottage cheese",   new NutritionData(98,  11, 3.4, 4.3, FridgeItem.FoodCategory.PROTEIN));
        FOOD_DB.put("shrimp",           new NutritionData(99,  24, 0.2, 0.3, FridgeItem.FoodCategory.PROTEIN));

        // Carbs
        FOOD_DB.put("rice",             new NutritionData(130, 2.7, 28, 0.3, FridgeItem.FoodCategory.CARB));
        FOOD_DB.put("pasta",            new NutritionData(131, 5,   25, 1.1, FridgeItem.FoodCategory.CARB));
        FOOD_DB.put("bread",            new NutritionData(265, 9,   49, 3.2, FridgeItem.FoodCategory.CARB));
        FOOD_DB.put("potato",           new NutritionData(77,  2,   17, 0.1, FridgeItem.FoodCategory.CARB));
        FOOD_DB.put("sweet potato",     new NutritionData(86,  1.6, 20, 0.1, FridgeItem.FoodCategory.CARB));
        FOOD_DB.put("oats",             new NutritionData(389, 17,  66, 7,   FridgeItem.FoodCategory.CARB));
        FOOD_DB.put("quinoa",           new NutritionData(120, 4.4, 21, 1.9, FridgeItem.FoodCategory.CARB));
        FOOD_DB.put("bread roll",       new NutritionData(280, 9,   52, 3,   FridgeItem.FoodCategory.CARB));
        FOOD_DB.put("tortilla",         new NutritionData(218, 6,   36, 5,   FridgeItem.FoodCategory.CARB));

        // Vegetables
        FOOD_DB.put("broccoli",         new NutritionData(34,  2.8, 7,  0.4, FridgeItem.FoodCategory.VEGETABLE));
        FOOD_DB.put("spinach",          new NutritionData(23,  2.9, 3.6,0.4, FridgeItem.FoodCategory.VEGETABLE));
        FOOD_DB.put("peppers",          new NutritionData(31,  1,   6,  0.3, FridgeItem.FoodCategory.VEGETABLE));
        FOOD_DB.put("onion",            new NutritionData(40,  1.1, 9,  0.1, FridgeItem.FoodCategory.VEGETABLE));
        FOOD_DB.put("garlic",           new NutritionData(149, 6.4, 33, 0.5, FridgeItem.FoodCategory.VEGETABLE));
        FOOD_DB.put("tomato",           new NutritionData(18,  0.9, 3.9,0.2, FridgeItem.FoodCategory.VEGETABLE));
        FOOD_DB.put("cucumber",         new NutritionData(15,  0.7, 3.6,0.1, FridgeItem.FoodCategory.VEGETABLE));
        FOOD_DB.put("carrot",           new NutritionData(41,  0.9, 10, 0.2, FridgeItem.FoodCategory.VEGETABLE));
        FOOD_DB.put("mushrooms",        new NutritionData(22,  3.1, 3.3,0.3, FridgeItem.FoodCategory.VEGETABLE));
        FOOD_DB.put("courgette",        new NutritionData(17,  1.2, 3.1,0.3, FridgeItem.FoodCategory.VEGETABLE));
        FOOD_DB.put("kale",             new NutritionData(49,  4.3, 9,  0.9, FridgeItem.FoodCategory.VEGETABLE));
        FOOD_DB.put("lettuce",          new NutritionData(15,  1.4, 2.9,0.2, FridgeItem.FoodCategory.VEGETABLE));

        // Fruits
        FOOD_DB.put("banana",           new NutritionData(89,  1.1, 23, 0.3, FridgeItem.FoodCategory.FRUIT));
        FOOD_DB.put("apple",            new NutritionData(52,  0.3, 14, 0.2, FridgeItem.FoodCategory.FRUIT));
        FOOD_DB.put("orange",           new NutritionData(47,  0.9, 12, 0.1, FridgeItem.FoodCategory.FRUIT));
        FOOD_DB.put("blueberries",      new NutritionData(57,  0.7, 14, 0.3, FridgeItem.FoodCategory.FRUIT));
        FOOD_DB.put("strawberries",     new NutritionData(32,  0.7, 8,  0.3, FridgeItem.FoodCategory.FRUIT));
        FOOD_DB.put("mango",            new NutritionData(60,  0.8, 15, 0.4, FridgeItem.FoodCategory.FRUIT));

        // Dairy
        FOOD_DB.put("milk",             new NutritionData(61,  3.2, 4.8,3.3, FridgeItem.FoodCategory.DAIRY));
        FOOD_DB.put("cheese",           new NutritionData(402, 25,  1.3,33,  FridgeItem.FoodCategory.DAIRY));
        FOOD_DB.put("butter",           new NutritionData(717, 0.9, 0.1,81,  FridgeItem.FoodCategory.FAT));
        FOOD_DB.put("yogurt",           new NutritionData(61,  3.5, 4.7,3.3, FridgeItem.FoodCategory.DAIRY));

        // Fats
        FOOD_DB.put("olive oil",        new NutritionData(884, 0,   0,  100, FridgeItem.FoodCategory.FAT));
        FOOD_DB.put("avocado",          new NutritionData(160, 2,   9,  15,  FridgeItem.FoodCategory.FAT));
        FOOD_DB.put("nuts",             new NutritionData(607, 20,  21, 54,  FridgeItem.FoodCategory.FAT));
        FOOD_DB.put("peanut butter",    new NutritionData(588, 25,  20, 50,  FridgeItem.FoodCategory.FAT));
        FOOD_DB.put("almonds",          new NutritionData(579, 21,  22, 50,  FridgeItem.FoodCategory.FAT));
    }

    /** Looks up a food by name and returns estimated nutrition. */
    public FoodLookupResult lookup(String name) {
        String key = name.toLowerCase().trim();

        // Direct match
        if (FOOD_DB.containsKey(key)) {
            NutritionData d = FOOD_DB.get(key);
            return new FoodLookupResult(d.cals(), d.protein(), d.carbs(), d.fat(), d.category());
        }

        // Partial match
        for (Map.Entry<String, NutritionData> entry : FOOD_DB.entrySet()) {
            if (entry.getKey().contains(key) || key.contains(entry.getKey())) {
                NutritionData d = entry.getValue();
                return new FoodLookupResult(d.cals(), d.protein(), d.carbs(), d.fat(), d.category());
            }
        }

        // No match
        return new FoodLookupResult(150, 5, 20, 5, FridgeItem.FoodCategory.OTHER);
    }

    public record FoodLookupResult(
        double calsPer100g,
        double proteinPer100g,
        double carbsPer100g,
        double fatPer100g,
        FridgeItem.FoodCategory category
    ) {}
}
