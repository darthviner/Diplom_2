package models;

import java.util.List;

public class Ingredients {
    private boolean success;
    private List<OrderData> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<OrderData> getIngredientData() {
        return data;
    }

    public void setIngredientData(List<OrderData> ingredientData) {
        this.data = ingredientData;
    }
}