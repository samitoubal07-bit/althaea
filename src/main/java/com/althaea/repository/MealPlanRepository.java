package com.althaea.repository;

import com.althaea.model.MealPlan;
import com.althaea.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByUserOrderByStartDateDesc(User user);
    Optional<MealPlan> findTopByUserOrderByStartDateDesc(User user);
}
