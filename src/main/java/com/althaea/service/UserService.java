package com.althaea.service;

import com.althaea.config.JwtService;
import com.althaea.dto.*;
import com.althaea.model.User;
import com.althaea.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Handles account creation, authentication, and profile updates. Passwords are bcrypt-hashed. Authentication returns a JWT */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        User user = User.builder()
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .name(req.name())
                .age(req.age())
                .weightKg(req.weightKg())
                .heightCm(req.heightCm())
                .sex(req.sex())
                .bodyGoal(req.bodyGoal())
                .fitnessGoal(req.fitnessGoal())
                .activityLevel(req.activityLevel())
                .build();

        userRepo.save(user);
        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getName(),
                user.getBodyGoal(), user.getFitnessGoal());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials."));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials.");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getName(),
                user.getBodyGoal(), user.getFitnessGoal());
    }

    public UserProfileResponse updateProfile(User user, UpdateProfileRequest req) {
        if (req.weightKg()       != null) user.setWeightKg(req.weightKg());
        if (req.bodyGoal()       != null) user.setBodyGoal(req.bodyGoal());
        if (req.fitnessGoal()    != null) user.setFitnessGoal(req.fitnessGoal());
        if (req.activityLevel()  != null) user.setActivityLevel(req.activityLevel());
        userRepo.save(user);
        return UserProfileResponse.from(user);
    }
}
