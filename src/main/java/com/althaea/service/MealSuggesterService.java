package com.althaea.service;

import com.althaea.model.FridgeItem;
import com.althaea.model.MealSuggestion;
import com.althaea.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/** MealSuggesterService Generates ameal suggestions from the user's available fridge items, based off their macro targets and fitness goal. */
@Service
public class MealSuggesterService {
    private static final int QUEUE_SIZE = 30;
    private static final int REGENERATE_THRESHOLD = 15;

    /** Generates a day's worth of meal suggestions using available fridge items. */
    public List<MealSuggestion> generateSwipeQueue(
        User user,
        List<FridgeItem> fridgeItems,
        MacroCalculatorService.MacroSplit macros,
        LocalDate date) {

        Map<FridgeItem.FoodCategory, List<FridgeItem>> grouped = fridgeItems.stream()
                .collect(Collectors.groupingBy(FridgeItem::getCategory));

        List<MealSuggestion> queue = new ArrayList<>();
        int position = 0;

        for (int i = 0; i < 9; i++) {
            MealSuggestion s = buildBreakfast(user, grouped, macros, date);
            s.setQueuePosition(position++);
            s.setSeen(false);
            queue.add(s);
        }
        for (int i = 0; i < 9; i++) {
            MealSuggestion s = buildLunch(user, grouped, macros, date);
            s.setQueuePosition(position++);
            s.setSeen(false);
            queue.add(s);
        }
        for (int i = 0; i < 9; i++) {
            MealSuggestion s = buildDinner(user, grouped, macros, date);
            s.setQueuePosition(position++);
            s.setSeen(false);
            queue.add(s);
        }
        for (int i = 0; i < 3; i++) {
            MealSuggestion s = buildSnack(user, grouped, macros, date);
            s.setQueuePosition(position++);
            s.setSeen(false);
            queue.add(s);
        }

        Collections.shuffle(queue);
        return queue;
    }

    // Meal builders

    private MealSuggestion buildBreakfast(
            User user,
            Map<FridgeItem.FoodCategory, List<FridgeItem>> grouped,
            MacroCalculatorService.MacroSplit macros,
            LocalDate date) {

        FridgeItem protein = pickRandom(grouped, FridgeItem.FoodCategory.PROTEIN);
        FridgeItem carb    = pickRandom(grouped, FridgeItem.FoodCategory.CARB);
        FridgeItem dairy   = pickRandom(grouped, FridgeItem.FoodCategory.DAIRY);

        String mealName = buildMealName("Breakfast Bowl",
                protein != null ? protein.getName() : null,
                carb    != null ? carb.getName()    : null,
                dairy   != null ? dairy.getName()   : null);

        String ingredients = buildIngredientList(protein, 150, carb, 80, dairy, 100, null, 0);
        int[] calculated   = estimateMacros(protein, 150, carb, 80, dairy, 100, null, 0);

        return MealSuggestion.builder()
                .mealDate(date)
                .mealType(MealSuggestion.MealType.BREAKFAST)
                .mealName(mealName)
                .ingredients(ingredients)
                .prepInstructions(generatePrepInstructions(MealSuggestion.MealType.BREAKFAST, protein, carb))
                .calories(calculated[0])
                .proteinG(calculated[1])
                .carbsG(calculated[2])
                .fatG(calculated[3])
                .prepTimeMinutes(10)
                .goalNote(buildGoalNote(user, MealSuggestion.MealType.BREAKFAST))
                .build();
    }

    private MealSuggestion buildLunch(
            User user,
            Map<FridgeItem.FoodCategory, List<FridgeItem>> grouped,
            MacroCalculatorService.MacroSplit macros,
            LocalDate date) {

        // Lunch: ~30% of daily calories
        FridgeItem protein = pickRandom(grouped, FridgeItem.FoodCategory.PROTEIN);
        FridgeItem carb    = pickRandom(grouped, FridgeItem.FoodCategory.CARB);
        FridgeItem veg     = pickRandom(grouped, FridgeItem.FoodCategory.VEGETABLE);

        String mealName = buildMealName("Power Lunch",
                protein != null ? protein.getName() : null,
                carb    != null ? carb.getName()    : null,
                veg     != null ? veg.getName()     : null);

        String ingredients = buildIngredientList(protein, 200, carb, 120, veg, 150, null, 0);
        int[] calculated   = estimateMacros(protein, 200, carb, 120, veg, 150, null, 0);

        return MealSuggestion.builder()
                .mealDate(date)
                .mealType(MealSuggestion.MealType.LUNCH)
                .mealName(mealName)
                .ingredients(ingredients)
                .prepInstructions(generatePrepInstructions(MealSuggestion.MealType.LUNCH, protein, carb))
                .calories(calculated[0])
                .proteinG(calculated[1])
                .carbsG(calculated[2])
                .fatG(calculated[3])
                .prepTimeMinutes(20)
                .goalNote(buildGoalNote(user, MealSuggestion.MealType.LUNCH))
                .build();
    }

    private MealSuggestion buildDinner(
            User user,
            Map<FridgeItem.FoodCategory, List<FridgeItem>> grouped,
            MacroCalculatorService.MacroSplit macros,
            LocalDate date) {

        // Dinner: ~35% of daily calories — largest meal
        FridgeItem protein = pickRandom(grouped, FridgeItem.FoodCategory.PROTEIN);
        FridgeItem carb    = pickRandom(grouped, FridgeItem.FoodCategory.CARB);
        FridgeItem veg     = pickRandom(grouped, FridgeItem.FoodCategory.VEGETABLE);
        FridgeItem fat     = pickRandom(grouped, FridgeItem.FoodCategory.FAT);

        String mealName = buildMealName("Dinner",
                protein != null ? protein.getName() : null,
                carb    != null ? carb.getName()    : null,
                veg     != null ? veg.getName()     : null);

        String ingredients = buildIngredientList(protein, 250, carb, 150, veg, 180, fat, 15);
        int[] calculated   = estimateMacros(protein, 250, carb, 150, veg, 180, fat, 15);

        return MealSuggestion.builder()
                .mealDate(date)
                .mealType(MealSuggestion.MealType.DINNER)
                .mealName(mealName)
                .ingredients(ingredients)
                .prepInstructions(generatePrepInstructions(MealSuggestion.MealType.DINNER, protein, veg))
                .calories(calculated[0])
                .proteinG(calculated[1])
                .carbsG(calculated[2])
                .fatG(calculated[3])
                .prepTimeMinutes(30)
                .goalNote(buildGoalNote(user, MealSuggestion.MealType.DINNER))
                .build();
    }

    private MealSuggestion buildSnack(
            User user,
            Map<FridgeItem.FoodCategory, List<FridgeItem>> grouped,
            MacroCalculatorService.MacroSplit macros,
            LocalDate date) {

        FridgeItem protein = pickRandom(grouped, FridgeItem.FoodCategory.PROTEIN);
        FridgeItem fruit   = pickRandom(grouped, FridgeItem.FoodCategory.FRUIT);

        String ingredients = buildIngredientList(protein, 80, fruit, 100, null, 0, null, 0);
        int[] calculated   = estimateMacros(protein, 80, fruit, 100, null, 0, null, 0);

        return MealSuggestion.builder()
                .mealDate(date)
                .mealType(MealSuggestion.MealType.SNACK)
                .mealName("Recovery Snack")
                .ingredients(ingredients)
                .prepInstructions("Combine ingredients and eat within 30 minutes of training.")
                .calories(calculated[0])
                .proteinG(calculated[1])
                .carbsG(calculated[2])
                .fatG(calculated[3])
                .prepTimeMinutes(5)
                .goalNote(buildGoalNote(user, MealSuggestion.MealType.SNACK))
                .build();
    }

    // Helpers

    private FridgeItem pickRandom(
            Map<FridgeItem.FoodCategory, List<FridgeItem>> grouped,
            FridgeItem.FoodCategory category) {

        List<FridgeItem> items = grouped.getOrDefault(category, Collections.emptyList());
        if (items.isEmpty()) return null;
        return items.get(new Random().nextInt(items.size()));
    }

    private String buildMealName(String base, String a, String b, String c) {
        List<String> parts = new ArrayList<>();
        if (a != null) parts.add(capitalize(a));
        if (b != null) parts.add(capitalize(b));
        if (c != null) parts.add(capitalize(c));
        if (parts.isEmpty()) return base;
        return String.join(" & ", parts) + " " + base;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /** Builds a simple human-readable ingredient list with portion sizes */
    private String buildIngredientList(
            FridgeItem a, int aG, FridgeItem b, int bG,
            FridgeItem c, int cG, FridgeItem d, int dG) {

        StringBuilder sb = new StringBuilder();
        if (a != null) sb.append("- ").append(aG).append("g ").append(a.getName()).append("\n");
        if (b != null) sb.append("- ").append(bG).append("g ").append(b.getName()).append("\n");
        if (c != null) sb.append("- ").append(cG).append("g ").append(c.getName()).append("\n");
        if (d != null && dG > 0) sb.append("- ").append(dG).append("g ").append(d.getName()).append("\n");
        if (sb.isEmpty()) sb.append("- Use whatever's available\n");
        return sb.toString().trim();
    }

    /** Estimates macros from portion weights */
    private int[] estimateMacros(
            FridgeItem a, int aG, FridgeItem b, int bG,
            FridgeItem c, int cG, FridgeItem d, int dG) {

        double cals = 0, protein = 0, carbs = 0, fat = 0;

        if (a != null) {
            cals    += a.getCalsPer100g()    * aG / 100;
            protein += a.getProteinPer100g() * aG / 100;
            carbs   += a.getCarbsPer100g()   * aG / 100;
            fat     += a.getFatPer100g()     * aG / 100;
        }
        if (b != null) {
            cals    += b.getCalsPer100g()    * bG / 100;
            protein += b.getProteinPer100g() * bG / 100;
            carbs   += b.getCarbsPer100g()   * bG / 100;
            fat     += b.getFatPer100g()     * bG / 100;
        }
        if (c != null) {
            cals    += c.getCalsPer100g()    * cG / 100;
            protein += c.getProteinPer100g() * cG / 100;
            carbs   += c.getCarbsPer100g()   * cG / 100;
            fat     += c.getFatPer100g()     * cG / 100;
        }
        if (d != null && dG > 0) {
            cals    += d.getCalsPer100g()    * dG / 100;
            protein += d.getProteinPer100g() * dG / 100;
            carbs   += d.getCarbsPer100g()   * dG / 100;
            fat     += d.getFatPer100g()     * dG / 100;
        }

        return new int[]{(int)cals, (int)protein, (int)carbs, (int)fat};
    }

    private String generatePrepInstructions(
            MealSuggestion.MealType type, FridgeItem protein, FridgeItem carb) {

        String proteinName = protein != null ? protein.getName() : "protein";
        String carbName    = carb    != null ? carb.getName()    : "carbs";

        return switch (type) {
            case BREAKFAST ->
                "1. Cook " + carbName + " as directed.\n" +
                "2. Prepare " + proteinName + " (scramble, poach, or pan-fry).\n" +
                "3. Combine and season to taste.";
            case LUNCH ->
                "1. Grill or pan-cook " + proteinName + " until done.\n" +
                "2. Cook or prepare " + carbName + ".\n" +
                "3. Assemble with vegetables and season with salt, pepper, and herbs.";
            case DINNER ->
                "1. Season and cook " + proteinName + " (bake at 200°C or pan-sear).\n" +
                "2. Roast or steam vegetables alongside.\n" +
                "3. Serve with " + carbName + ". Rest protein for 5 minutes before plating.";
            case SNACK ->
                "1. Portion out ingredients.\n" +
                "2. Eat within 30 minutes of training for best recovery.";
        };
    }
}
