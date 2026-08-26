package com.althaea.controller;
import com.althaea.dto.FridgeItemRequest;
import com.althaea.model.FridgeItem;
import com.althaea.model.User;
import com.althaea.repository.FridgeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/fridge")
@RequiredArgsConstructor
public class FridgeController {
    private final FridgeItemRepository fridgeRepo;
    @GetMapping
    public ResponseEntity<List<FridgeItem>> getItems(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(fridgeRepo.findByUser(user));
    }
    @PostMapping
    public ResponseEntity<FridgeItem> addItem(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody FridgeItemRequest req) {
        FridgeItem item = FridgeItem.builder()
                .user(user)
                .name(req.name())
                .quantity(req.quantity())
                .unit(req.unit())
                .calsPer100g(req.calsPer100g())
                .proteinPer100g(req.proteinPer100g())
                .carbsPer100g(req.carbsPer100g())
                .fatPer100g(req.fatPer100g())
                .category(req.category())
                .build();
        return ResponseEntity.ok(fridgeRepo.save(item));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        fridgeRepo.findByIdAndUser(id, user)
                .ifPresent(fridgeRepo::delete);
        return ResponseEntity.noContent().build();
    }
}
