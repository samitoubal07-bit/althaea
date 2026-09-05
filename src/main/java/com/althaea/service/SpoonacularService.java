package com.althaea.service;

import com.althaea.model.MealSuggestion;
import com.althaea.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpoonacularService {

    @Value("${spoonacular.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<MealSuggestion> findRecipes(
            User user,
            List<String> ingredients,
            MealSuggestion.MealType mealType,
            int number) {

        System.out.println("Using API key: " + apiKey);
        System.out.println("Ingredients: " + ingredients);

        List<MealSuggestion> suggestions = new ArrayList<>();

        try {
            String ingredientList = String.join(",+", ingredients);
            String searchUrl = "https://api.spoonacular.com/recipes/findByIngredients"
                    + "?ingredients=" + ingredientList
                    + "&number=" + number
                    + "&ranking=1"
                    + "&ignorePantry=true"
                    + "&apiKey=" + apiKey;

            System.out.println("Calling: " + searchUrl);

            String searchResponse = restTemplate.getForObject(searchUrl, String.class);
            JsonNode recipes = mapper.readTree(searchResponse);

            System.out.println("Got " + recipes.size() + " recipes");

            for (JsonNode recipe : recipes) {
                long recipeId = recipe.get("id").asLong();
                String title = recipe.get("title").asText();
                String image = recipe.get("image").asText();

                String infoUrl = "https://api.spoonacular.com/recipes/" + recipeId
                        + "/information?includeNutrition=true&apiKey=" + apiKey;

                String infoResponse = restTemplate.getForObject(infoUrl, String.class);
                JsonNode info = mapper.readTree(infoResponse);

                int calories = 0, protein = 0, carbs = 0, fat = 0;
                JsonNode nutrients = info.path("nutrition").path("nutrients");
                for (JsonNode nutrient : nutrients) {
                    String name = nutrient.get("name").asText();
                    int amount = nutrient.get("amount").asInt();
                    switch (name) {
                        case "Calories" -> calories = amount;
                        case "Protein"  -> protein  = amount;
                        case "Carbohydrates" -> carbs = amount;
                        case "Fat"      -> fat     = amount;
                    }
                }

                StringBuilder ingredientsSb = new StringBuilder();
                for (JsonNode ing : info.path("extendedIngredients")) {
                    ingredientsSb.append("- ").append(ing.get("original").asText()).append("\n");
                }

                String instructions = info.path("instructions").asText("See recipe for instructions.");
                if (instructions.isEmpty()) instructions = "See recipe for instructions.";

                MealSuggestion suggestion = MealSuggestion.builder()
                        .mealType(mealType)
                        .mealName(title)
                        .imageUrl(image)
                        .ingredients(ingredientsSb.toString().trim())
                        .prepInstructions(instructions)
                        .calories(calories)
                        .proteinG(protein)
                        .carbsG(carbs)
                        .fatG(fat)
                        .prepTimeMinutes(info.path("readyInMinutes").asInt(30))
                        .goalNote(goalNote)
                        .seen(false)
                        .queuePosition(suggestions.size())
                        .build();

                suggestions.add(suggestion);
            }

        } catch (Exception e) {
            System.err.println("Spoonacular error: " + e.getMessage());
        }

        return suggestions;
    }
}
