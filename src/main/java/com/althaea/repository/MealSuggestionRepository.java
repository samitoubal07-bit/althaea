package com.althaea.repository;
import com.althaea.model.MealPlan;
import com.althaea.model.MealSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface MealSuggestionRepository extends JpaRepository<MealSuggestion, Long> {
    List<MealSuggestion> findByMealPlan(MealPlan plan);
    List<MealSuggestion> findByMealPlanAndMealDate(MealPlan plan, LocalDate date);
    List<MealSuggestion> findByMealPlanAndSeenFalse(MealPlan plan);
    List<MealSuggestion> findByMealPlanAndMealTypeAndAcceptedTrue(MealPlan plan, MealSuggestion.MealType type);
    long countByMealPlanAndSeenFalse(MealPlan plan);
}
