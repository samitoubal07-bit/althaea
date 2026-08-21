package com.althaea.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/** Generates 7 day meal plan for user */
@Entity
@Table(name = "meal_plans")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate startDate;
    private LocalDate endDate;

    /** The user's body + fitness goal combo at time of generation */
    @Enumerated(EnumType.STRING)
    private User.BodyGoal bodyGoal;

    @Enumerated(EnumType.STRING)
    private User.FitnessGoal fitnessGoal;

    /** Daily calorie target calculated by MacroCalculatorService */
    private int dailyCalorieTarget;

    private int dailyProteinG;
    private int dailyCarbsG;
    private int dailyFatG;

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealSuggestion> suggestions;

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealSuggestion> suggestionsRejected;
}
