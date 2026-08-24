package com.althaea.dto;

import com.althaea.model.User;
public record UserProfileResponse(
        Long id,
        String email,
        String name,
        int age,
        double weightKg,
        double heightCm,
        User.Sex sex,
        User.BodyGoal bodyGoal,
        User.FitnessGoal fitnessGoal,
        User.ActivityLevel activityLevel
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(), user.getEmail(), user.getName(),
                user.getAge(), user.getWeightKg(), user.getHeightCm(),
                user.getSex(), user.getBodyGoal(), user.getFitnessGoal(),
                user.getActivityLevel()
        );
    }
}
