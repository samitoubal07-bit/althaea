package com.althaea.controller;
import com.althaea.model.MealPlan;
import com.althaea.model.MealSuggestion;
import com.althaea.model.User;
import com.althaea.repository.MealPlanRepository;
import com.althaea.repository.MealSuggestionRepository;
import com.althaea.service.MacroCalculatorService;
import com.althaea.service.PlanGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanController {
    private final PlanGeneratorService planGenerator;
    private final MealPlanRepository planRepo;
    private final MacroCalculatorService macroCalculator;
    private final MealSuggestionRepository mealSuggestionRepo;
    @PostMapping("/generate")
    public ResponseEntity<MealPlan> generatePlan(@AuthenticationPrincipal User user) {
        MealPlan plan = planGenerator.generateWeeklyPlan(user);
        return ResponseEntity.ok(plan);
    }
    @GetMapping("/next-card")
    public ResponseEntity<MealSuggestion> getNextCard(@AuthenticationPrincipal User user) {
        return planRepo.findTopByUserOrderByStartDateDesc(user)
                .flatMap(plan -> mealSuggestionRepo
                        .findByMealPlanAndSeenFalse(plan)
                        .stream().findFirst())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    @PostMapping("/swipe/accept/{id}")
    public ResponseEntity<Void> acceptMeal(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        mealSuggestionRepo.findById(id).ifPresent(meal -> {
            meal.setAccepted(true);
            meal.setSeen(true);
            mealSuggestionRepo.save(meal);
        });
        return ResponseEntity.ok().build();
    }
    @PostMapping("/swipe/reject/{id}")
    public ResponseEntity<Void> rejectMeal(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        mealSuggestionRepo.findById(id).ifPresent(meal -> {
            meal.setAccepted(false);
            meal.setSeen(true);
            mealSuggestionRepo.save(meal);
        });
        return ResponseEntity.ok().build();
    }
    @GetMapping("/queue/remaining")
    public ResponseEntity<Long> remainingCards(@AuthenticationPrincipal User user) {
        return planRepo.findTopByUserOrderByStartDateDesc(user)
                .map(plan -> ResponseEntity.ok(mealSuggestionRepo.countByMealPlanAndSeenFalse(plan)))
                .orElse(ResponseEntity.noContent().build());
    }
    @GetMapping("/current")
    public ResponseEntity<MealPlan> getCurrentPlan(@AuthenticationPrincipal User user) {
        return planRepo.findTopByUserOrderByStartDateDesc(user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    @GetMapping("/macros")
    public ResponseEntity<MacroCalculatorService.MacroSplit> getMacros(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(macroCalculator.calculateMacros(user));
    }
}
