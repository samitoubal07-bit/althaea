package com.althaea.dto;
import com.althaea.model.User;
public record AuthResponse(
        String token,
        String email,
        String name,
        User.BodyGoal bodyGoal,
        User.FitnessGoal fitnessGoal
) {}
