package com.althaea.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/** A single suggested meal created from what's in the user's fridge to hit daily macro targets */
@Entity
@Table(name = "meal_suggestions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MealSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    private LocalDate mealDate;

    @Enumerated(EnumType.STRING)
    private MealType mealType;

    private String mealName;

    /** Difficulty rating 1-5, 1 = very easy, 5 = complex */
    private int difficultyRating;

    /** Notes on any specialist ingredients needed */
    private String specialistIngredients;

    /** URL to an image of the dish */
    private String imageUrl;

    /** Whether the user accepted or rejected depending on how they swiped */
    private Boolean accepted;

    /** Position in the suggestion queue */
    private int queuePosition;

    /** Whether the user has seen this suggestion yet */
    private boolean seen;

    /** Ingredient list with quantities */
    @Column(columnDefinition = "TEXT")
    private String ingredients;

    /** Brief preperation instructions */
    @Column(columnDefinition = "TEXT")
    private String prepInstructions;

    private int calories;
    private int proteinG;
    private int carbsG;
    private int fatG;
    private int prepTimeMinutes;

    /** Why this meal suits the user's goal */
    private String goalNote;

    public enum MealType { BREAKFAST, LUNCH, DINNER, SNACK }
}
