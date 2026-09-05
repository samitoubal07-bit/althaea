package com.althaea.service;

import com.althaea.model.*;
import com.althaea.repository.FridgeItemRepository;
import com.althaea.repository.MealPlanRepository;
import com.althaea.repository.WorkoutLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Uses Spoonacular to find recipes that match the ingredients in the users fridge. */
@Service
@RequiredArgsConstructor
public class PlanGeneratorService {

    private final MacroCalculatorService macroCalculator;
    private final SpoonacularService     spoonacular;
    private final FridgeItemRepository   fridgeItemRepo;
    private final WorkoutLogRepository   workoutLogRepo;
    private final MealPlanRepository     mealPlanRepo;

    @Transactional
    public MealPlan generateWeeklyPlan(User user) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate   = startDate.plusDays(6);

        // Get ingredient names from fridge
        List<FridgeItem> fridgeItems = fridgeItemRepo.findByUser(user);
        List<String> ingredients = fridgeItems.stream()
                .map(FridgeItem::getName)
                .collect(Collectors.toList());

        // Calculate base macros
        MacroCalculatorService.MacroSplit baseMacros = macroCalculator.calculateMacros(user);

        MealPlan plan = MealPlan.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .bodyGoal(user.getBodyGoal())
                .fitnessGoal(user.getFitnessGoal())
                .dailyCalorieTarget(baseMacros.totalCalories())
                .dailyProteinG(baseMacros.proteinG())
                .dailyCarbsG(baseMacros.carbsG())
                .dailyFatG(baseMacros.fatG())
                .suggestions(new ArrayList<>())
                .rejectedSuggestions(new ArrayList<>())
                .build();

        // Fetch recipes from Spoonacular
        List<MealSuggestion> queue = new ArrayList<>();
        queue.addAll(spoonacular.findRecipes(user, ingredients, MealSuggestion.MealType.BREAKFAST, 3));
        queue.addAll(spoonacular.findRecipes(user, ingredients, MealSuggestion.MealType.LUNCH, 3));
        queue.addAll(spoonacular.findRecipes(user, ingredients, MealSuggestion.MealType.DINNER, 3));
        queue.addAll(spoonacular.findRecipes(user, ingredients, MealSuggestion.MealType.SNACK, 3));

        // Set position and link to plan
        for (int i = 0; i < queue.size(); i++) {
            queue.get(i).setQueuePosition(i);
            queue.get(i).setMealPlan(plan);
            queue.get(i).setMealDate(startDate);
        }

        java.util.Collections.shuffle(queue);
        plan.getSuggestions().addAll(queue);

        return mealPlanRepo.save(plan);
    }
}
