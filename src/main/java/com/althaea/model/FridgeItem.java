package com.althaea.model;

import jakarta.persistence.*;
import lombok.*;

/** Represents one ingredient the user currently has in their fridge, uses this information to identify meals using the ingredients */
@Entity
@Table(name = "fridge_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FridgeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;           // e.g. "chicken breast"

    private double quantity;       // e.g. 500
    private String unit;           // e.g. "g"

    @Column(name = "calories_per_100g")
    private double calsPer100g;

    @Column(name = "protein_per_100g")
    private double proteinPer100g;

    @Column(name = "carbs_per_100g")
    private double carbsPer100g;

    @Column(name = "fat_per_100g")
    private double fatPer100g;

    /** Groups foods in order to suggest meals */
    @Enumerated(EnumType.STRING)
    private FoodCategory category;

    public enum FoodCategory {
        PROTEIN, CARB, VEGETABLE, FRUIT, DAIRY, FAT, OTHER
    }
}
