package utils;

import io.qameta.allure.Step;
import models.Ingredients;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class IngredientsClient extends Client{
    private final String INGREDIENTS_PATH = "/api/ingredients";

    @Step("Get ingredients IDs")
    public List<String> getIngredientsIDs() {
        Ingredients ingredientsIDs =  given()
                .spec(getSpec())
                .get(INGREDIENTS_PATH).body().as(Ingredients.class);
        return getIngredientId(ingredientsIDs);
    }

    public List<String> getIngredientId(Ingredients ingredients) {
        return ingredients.getIngredientData().stream().
                map(data -> data.get_id()).collect(Collectors.toList());
    }
}