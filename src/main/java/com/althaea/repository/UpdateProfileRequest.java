package com.althaea.dto;

import com.althaea.model.User;
public record UpdateProfileRequest(
        Double weightKg,
        User.BodyGoal bodyGoal,
        User.FitnessGoal fitnessGoal,
        User.ActivityLevel activityLevel
) {}
