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

/**  Generates a completed meal plan based off the user's data and goals */
@Service
@RequiredArgsConstructor
public class PlanGeneratorService {

    private final MacroCalculatorService macroCalculator;
    private final MealSuggesterService   mealSuggester;
    private final FridgeItemRepository   fridgeItemRepo;
    private final WorkoutLogRepository   workoutLogRepo;
    private final MealPlanRepository     mealPlanRepo;

    /** Generates a 7-day meal plan starting from today. */
    @Transactional
    public MealPlan generateWeeklyPlan(User user) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate   = startDate.plusDays(6);

        // Load user's fridge
        List<FridgeItem> fridgeItems = fridgeItemRepo.findByUser(user);

        // Load workouts for the upcoming 7 days
        List<WorkoutLog> upcomingWorkouts = workoutLogRepo
                .findByUserAndWorkoutDateBetween(user, startDate, endDate);

        // Calculate base daily macros
        MacroCalculatorService.MacroSplit baseMacros = macroCalculator.calculateMacros(user);

        // Build the plan shell
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

        // Generate meals for each day
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);

            // Find if there's a workout this day and adjust macros accordingly
            Optional<WorkoutLog> workoutToday = upcomingWorkouts.stream()
                    .filter(w -> w.getWorkoutDate().equals(date))
                    .findFirst();

            MacroCalculatorService.MacroSplit dayMacros = workoutToday
                    .map(w -> macroCalculator.calculateMacrosForWorkoutDay(user, w.getType()))
                    .orElse(baseMacros);

            // Generate meals for this day
            List<MealSuggestion> dayMeals = mealSuggester.generateSwipeQueue(
                user, fridgeItems, dayMacros, date);

            // Link suggestions back to the plan
            dayMeals.forEach(meal -> meal.setMealPlan(plan));
            plan.getSuggestions().addAll(dayMeals);
        }

        return mealPlanRepo.save(plan);
    }
}
