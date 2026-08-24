package com.althaea.repository;

import com.althaea.model.FridgeItem;
import com.althaea.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface FridgeItemRepository extends JpaRepository<FridgeItem, Long> {
    List<FridgeItem> findByUser(User user);
    Optional<FridgeItem> findByIdAndUser(Long id, User user);
    List<FridgeItem> findByUserAndCategory(User user, FridgeItem.FoodCategory category);
}
