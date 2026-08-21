package com.althaea.repository;

import com.althaea.model.WorkoutLog;
import com.althaea.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findByUser(User user);
    List<WorkoutLog> findByUserOrderByWorkoutDateDesc(User user);
    List<WorkoutLog> findByUserAndWorkoutDateBetween(User user, LocalDate start, LocalDate end);
}
