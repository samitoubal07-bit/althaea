package com.althaea.controller;

import com.althaea.model.FridgeItem;
import com.althaea.model.User;
import com.althaea.repository.FridgeItemRepository;
import com.althaea.service.FoodLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fridge")
@RequiredArgsConstructor
public class FridgeController {

    private final FridgeItemRepository fridgeRepo;
    private final FoodLookupService foodLookup;

    @GetMapping
    public ResponseEntity<List<FridgeItem>> getItems(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(fridgeRepo.findByUser(user));
    }

    /** User just provides ingredient name — no quantity or nutrition needed */
    @PostMapping
    public ResponseEntity<FridgeItem> addItem(
            @AuthenticationPrincipal User user,
            @RequestBody SimpleIngredientRequest req) {

        FoodLookupService.FoodLookupResult nutrition = foodLookup.lookup(req.name());

        FridgeItem item = FridgeItem.builder()
                .user(user)
                .name(req.name())
                .quantity(100)
                .unit("g")
                .calsPer100g(nutrition.calsPer100g())
                .proteinPer100g(nutrition.proteinPer100g())
                .carbsPer100g(nutrition.carbsPer100g())
                .fatPer100g(nutrition.fatPer100g())
                .category(nutrition.category())
                .build();

        return ResponseEntity.ok(fridgeRepo.save(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        fridgeRepo.findByIdAndUser(id, user).ifPresent(fridgeRepo::delete);
        return ResponseEntity.noContent().build();
    }

    public record SimpleIngredientRequest(String name) {}
}
