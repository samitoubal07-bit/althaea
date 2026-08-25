package com.althaea.service;

import com.althaea.model.User;
import com.althaea.model.WorkoutLog;
import org.springframework.stereotype.Service;

/**
 Calculates a user's daily calorie target and macro split based on the users physical data, activity levels, fitness and body goals */
@Service
public class MacroCalculatorService {

    /** Calculates daily calorie target from user profile */
    public int calculateDailyCalories(User user) {
        double bmr = calculateBMR(user);
        double tdee = bmr * getActivityMultiplier(user.getActivityLevel());
        double adjusted = tdee * getBodyGoalMultiplier(user.getBodyGoal());
        return (int) Math.round(adjusted);
    }

    /** Returns a MacroSplit record with protein/carbs/fat in grams */
    public MacroSplit calculateMacros(User user) {
        int calories = calculateDailyCalories(user);
        return getMacroSplit(calories, user.getBodyGoal(), user.getFitnessGoal());
    }

    /** Adjusts calories for same-day workout intensity */
    public MacroSplit calculateMacrosForWorkoutDay(User user, WorkoutLog.WorkoutType workoutType) {
        int baseCalories = calculateDailyCalories(user);
        int adjustedCalories = (int)(baseCalories * getWorkoutDayMultiplier(workoutType));
        MacroSplit base = getMacroSplit(adjustedCalories, user.getBodyGoal(), user.getFitnessGoal());

        // Shift more calories to carbs on high-intensity days
        if (workoutType == WorkoutLog.WorkoutType.HIIT || workoutType == WorkoutLog.WorkoutType.CARDIO) {
            int extraCarbs = (int)(adjustedCalories * 0.05 / 4); // 5% more cals from carbs
            return new MacroSplit(adjustedCalories, base.proteinG(), base.carbsG() + extraCarbs, base.fatG());
        }
        if (workoutType == WorkoutLog.WorkoutType.STRENGTH) {
            int extraProtein = (int)(adjustedCalories * 0.05 / 4);
            return new MacroSplit(adjustedCalories, base.proteinG() + extraProtein, base.carbsG(), base.fatG());
        }
        return base;
    }

    //  Private helpers

    /** Mifflin-St Jeor BMR equation */
    private double calculateBMR(User user) {
        double bmr = (10 * user.getWeightKg())
                   + (6.25 * user.getHeightCm())
                   - (5.0 * user.getAge());

        return switch (user.getSex()) {
            case MALE  -> bmr + 5;
            case FEMALE -> bmr - 161;
        };
    }

    private double getActivityMultiplier(User.ActivityLevel level) {
        if (level == null) return 1.375; // default to lightly active
        return switch (level) {
            case SEDENTARY          -> 1.2;
            case LIGHTLY_ACTIVE     -> 1.375;
            case MODERATELY_ACTIVE  -> 1.55;
            case ACTIVE             -> 1.725;
            case EXTREMELY_ACTIVE   -> 1.9;
        };
    }

    /**
     * Body goal multiplier: CUT      = -20% calories (aggressive but safe deficit), MAINTAIN =   0% (TDEE), BULK     = +15% calories (lean bulk surplus) */
    private double getBodyGoalMultiplier(User.BodyGoal goal) {
        return switch (goal) {
            case CUT      -> 0.80;
            case MAINTAIN -> 1.00;
            case BULK     -> 1.15;
        };
    }

    private double getWorkoutDayMultiplier(WorkoutLog.WorkoutType type) {
        return switch (type) {
            case STRENGTH    -> 1.05; // slight surplus for recovery
            case HIIT        -> 1.08; // high demand
            case CARDIO      -> 1.06;
            case YOGA_MOBILITY -> 1.0;
            case REST        -> 0.95; // mild deficit on rest days
        };
    }

    /**
     * Macro split percentages by fitness goal and body goal. MUSCLE_GAIN:  Protein ~35%, Carbs ~40%, Fat ~25%, ENDURANCE:    Protein ~20%, Carbs ~55%, Fat ~25%, FAT_LOSS:     Protein ~40%, Carbs ~25%, Fat ~35%, On a BULK these are weighted slightly more carbs/protein. On a CUT they are weighted toward higher protein to preserve muscle. */
    private MacroSplit getMacroSplit(int calories, User.BodyGoal bodyGoal, User.FitnessGoal fitnessGoal) {
        double proteinPct;
        double carbsPct;
        double fatPct;

        // Base split by fitness goal
        switch (fitnessGoal) {
            case MUSCLE_GAIN -> { proteinPct = 0.35; carbsPct = 0.40; fatPct = 0.25; }
            case ENDURANCE   -> { proteinPct = 0.20; carbsPct = 0.55; fatPct = 0.25; }
            default          -> { proteinPct = 0.40; carbsPct = 0.25; fatPct = 0.35; } // FAT_LOSS
        }

        // Adjust for body goal
        if (bodyGoal == User.BodyGoal.CUT) {
            proteinPct += 0.05;  // more protein to preserve muscle in a deficit
            carbsPct   -= 0.05;
        } else if (bodyGoal == User.BodyGoal.BULK) {
            carbsPct   += 0.05;  // more carbs to fuel growth
            fatPct     -= 0.05;
        }

        // Convert percentages to grams
        // Protein and carbs: 4 kcal/g | Fat: 9 kcal/g
        int proteinG = (int) Math.round((calories * proteinPct) / 4);
        int carbsG   = (int) Math.round((calories * carbsPct)   / 4);
        int fatG     = (int) Math.round((calories * fatPct)     / 9);

        return new MacroSplit(calories, proteinG, carbsG, fatG);
    }

    /** Immutable value object returned by macro calculations */
    public record MacroSplit(int totalCalories, int proteinG, int carbsG, int fatG) {}
}
